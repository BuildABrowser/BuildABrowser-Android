package net.buildabrowser.droided.network.imp

import net.buildabrowser.babbrowser.common.util.CommonUtil
import net.buildabrowser.babbrowser.fetch.FetchBackend
import net.buildabrowser.babbrowser.fetch.FetchBody
import net.buildabrowser.babbrowser.fetch.FetchRequest
import net.buildabrowser.babbrowser.fetch.FetchResponse
import net.buildabrowser.babbrowser.fetch.HeaderList
import net.buildabrowser.babbrowser.fetch.imp.FetchImpUtil
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse
import net.buildabrowser.babbrowser.network.ExtensionUtil
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.util.Optional
import java.util.function.Consumer

const val CHROME_UA_STRING
        : String = ("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
        + " Chrome/146.0.0.0 Safari/537.36")

// Seems the only NIO callback-based library is Cronet. It requires Play Store, so doesn't work on some Android forks
// Plus, it's ripped from Chromium, and I'm not exactly making Chromium 2. Bad enough that I'm already using Skia and Gson
// Somebody please make a NIO callback-based HTTP library, so I don't have to do so
// Just using OkHttp until then ):
// TODO: Also allow using the ContentEncodingRegistry here
class FetchBackendImp() : FetchBackend {

    private val httpClient = OkHttpClient()

    override fun makeRequest(
        fetchResponse: MutableFetchResponse,
        request: FetchRequest,
        byteConsumer: Consumer<Optional<ByteBuffer>>
    ) {
        // TODO: Correct way to set origin
        val url = request.currentURL()
        var origin = url.scheme + "://" + url.host
        if (!(
            (url.scheme == "https" && url.port == 443)
            || (url.scheme == "http" && url.port == 80)
            || url.port == -1
        )) {
            origin = origin + ":" + url.port
        }

        // TODO: Include the headers
        println(request.method())
        val httpRequestBuilder =
            Request.Builder()
                .url(request.currentURL().toString())
                .method(request.method(), createRequestBody(request))
                .header("User-Agent", chooseUserAgent(request))
                .header("Accept", "text/html, text/css, image/png, image/jpeg, */*")
                .header("Sec-CH-UA", "\"BuildABrowser Test Program\";v=\"0\"")
                .header("Origin", origin)

        request.headerList().forEach(httpRequestBuilder::header);

        val httpRequest = httpRequestBuilder.build()
        httpClient
            .newCall(httpRequest)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    fetchResponse.urlList()
                        .add(request.currentURL()) // TODO: Is this handled elsewhere, for recursive requests?
                    fetchResponse.setStatus(response.code)
                    appendResponseHeaders(fetchResponse, response)

                    val source = response.body.source()
                    val sink = Buffer()
                    var readBytes = source.read(sink, CHUNK_SIZE)
                    while (readBytes != -1L) {
                        // TODO: ...and that's a copy...
                        val buffer = ByteBuffer.wrap(sink.readByteArray(), 0, readBytes.toInt())
                        byteConsumer.accept(Optional.of(buffer))
                        readBytes = source.read(sink, CHUNK_SIZE)
                    }
                    byteConsumer.accept(Optional.empty())
                }

                override fun onFailure(call: Call, e: okio.IOException) {
                    LOGGER.error("An issue occured while handling a network packet!", e)
                    // TODO: Proper exception handling
                }
            })
    }

    // TODO: Need to test if this works on Android, ported from desktop
    override fun fetchFile(request: FetchRequest): FetchResponse {
        // TODO: Improve security
        val file = CommonUtil.tryOrNull { File(request.url()) }
        if (file == null || !file.exists() || file.isDirectory()) {
            return FetchResponse.createNetworkError()
        }

        try {
            val bytes = Files.readAllBytes(file.toPath())
            var mimeType = ExtensionUtil.guessMimeTypeFromFileName(file.path)
            if (mimeType == null) {
                mimeType = "application/octet-stream"
            }
            return FetchResponse.create(
                "OK",
                HeaderList.create("Content-Type", mimeType),
                FetchImpUtil.getBytesAsABody(bytes)
            )
        } catch (_: IOException) {
            return FetchResponse.createNetworkError()
        }
    }

    private fun appendResponseHeaders(response: MutableFetchResponse, responseInfo: Response) {
        val headerList = response.headerList()
        for (headerEntry in responseInfo.headers) {
            headerList.append(headerEntry.first, headerEntry.second)
        }
    }

    private fun createRequestBody(request: FetchRequest): RequestBody? {
        if (request.body() == null) {
            return null
        }

        val body = request.body() as FetchBody
        val reader = body.stream.getReader(null) as ReadableStreamDefaultReader

        val contentType: MediaType? = request.headerList().get("content-type").toMediaTypeOrNull()
        return StreamReaderRequestBody(body, reader, contentType)
    }

    // Unfortunately DDG captchas the user with the default UA (and captchas would require JS)
    private fun chooseUserAgent(request: FetchRequest): String {
        val uaTemplate = when (request.url().getHost()) {
            "html.duckduckgo.com", "duckduckgo.com" -> CHROME_UA_STRING + " BABBrowser/0.1.0"
            "news.ycombinator.com" -> CHROME_UA_STRING
            "whatismybrowser.com", "www.whatismybrowser.com" -> "BABBrowser/0.1.0 (%OS)"
            "buildabrowser.net", "frogfind.de" -> "Mozilla/5.0 (%OS) BABBrowser/0.1.0"
            else -> "Mozilla/5.0 (&OS) BABBrowser/0.1.0 Firefox/149.0 (Not actually Firefox)"
        }
        val osInfo = "Linux; Android 10; K"
        return uaTemplate.replace("%OS", osInfo)
    }

    companion object {
        private const val CHUNK_SIZE = 2048L
        private val LOGGER: Logger = LoggerFactory.getLogger(FetchBackendImp::class.java)
    }
}
