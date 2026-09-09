package net.buildabrowser.droided.network.imp

import net.buildabrowser.babbrowser.fetch.FetchBody
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException


class StreamReaderRequestBody(
    private val body: FetchBody,
    private val reader: ReadableStreamDefaultReader,
    private val contentType: MediaType?
) : RequestBody() {

    override fun contentType(): MediaType? {
        return contentType
    }

    override fun contentLength(): Long {
        return body.length.toLong()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val future = CompletableFuture<ByteArray?>()

        reader.readAllBytes(
            { t: ByteArray? -> future.complete(t) },
            { err: Any? ->
                future.completeExceptionally(
                    err as? Throwable
                        ?: if (err is String) IOException(err) else IOException(
                            "Reading body completed abnormally"
                        )
                )
            }
        )

        try {
            val bytes = future.get()
            sink.write(bytes!!)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Interrupted while reading stream body", e)
        } catch (e: ExecutionException) {
            val cause = e.cause
            if (cause is IOException) {
                throw cause
            }
            throw IOException("Failed to read body", cause)
        }
    }
}