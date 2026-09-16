package net.buildabrowser.babbrowser.embedding.android

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import net.buildabrowser.babbrowser.cookies.stores.InMemoryCookieStore
import net.buildabrowser.babbrowser.embedding.android.network.imp.BasicUAChooserImp
import net.buildabrowser.babbrowser.embedding.android.network.imp.FetchBackendImp
import net.buildabrowser.babbrowser.embedding.android.ui.AndroidClipboardProvider
import net.buildabrowser.babbrowser.embedding.android.ui.NoOpDownloadManager
import net.buildabrowser.babbrowser.embedding.android.ui.NoOpTabManager
import net.buildabrowser.babbrowser.embedding.android.ui.component.FrameGUI
import net.buildabrowser.babbrowser.embedding.android.ui.input.AndroidVirtualKeyboard
import net.buildabrowser.babbrowser.fetch.FetchPolicy
import net.buildabrowser.babbrowser.painter.android.AndroidSkiaComposePainter
import net.buildabrowser.babbrowser.renderer.RenderingEngine
import net.buildabrowser.babbrowser.renderer.RenderingEngineBuilder
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry
import java.net.URI
import java.util.concurrent.Executors

object AndroidEmbedding {

    fun createRenderingEngine(
        context: Context
    ) : RenderingEngine {
        val builder = RenderingEngineBuilder.create()
        configure(builder, context)
        return builder.build()
    }

    fun configure(
        builder: RenderingEngineBuilder,
        context: Context
    ) {
        val fetchBackend = FetchBackendImp()
        val loaderRegistry = DocumentLoaderRegistry.createDefault()
        val cookieStore = InMemoryCookieStore { false }
        val uaChooser = BasicUAChooserImp()
        val painter = AndroidSkiaComposePainter()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipboardProvider = AndroidClipboardProvider(clipboard, context)
        val virtualKeyboardFactory = ::AndroidVirtualKeyboard
        val tabManager = NoOpTabManager()
        val downloadManager = NoOpDownloadManager()

        builder
            .setFetchBackend(fetchBackend)
            .setFetchPolicy(object : FetchPolicy {})
            .setCookieStore(cookieStore)
            .setUAChooser(uaChooser)
            .setThreadGroupSupplier(Executors::newWorkStealingPool)
            .setPainter(painter)
            .setDocumentLoaderRegistry(loaderRegistry)
            .setResourceResolver(context.assets::open)
            .setClipboardProvider(clipboardProvider)
            .setVirtualKeyboardFactory(virtualKeyboardFactory)
            .setTabManager(tabManager)
            .setDownloadManager(downloadManager)
    }

}

@Composable
fun EmbedFrame(
    context: Context,
    url: String,
    modifier: Modifier
) {
    val frame = remember {
        val renderingEngine = AndroidEmbedding.createRenderingEngine(context)
        val frame = renderingEngine.createFrame()
        frame.navigate(URI(url))
        frame
    }

    val frameGUI = FrameGUI(frame)
    return frameGUI.FrameComponent(modifier)
}