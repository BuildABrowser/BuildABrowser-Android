package net.buildabrowser.droided

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import net.buildabrowser.babbrowser.painter.core.Painter
import net.buildabrowser.babbrowser.renderer.RenderingEngine
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry
import net.buildabrowser.babbrowser.renderer.uistate.Frame
import net.buildabrowser.droided.network.imp.FetchBackendImp
import net.buildabrowser.droided.painter.androidskia.AndroidSkiaComposePainter
import net.buildabrowser.droided.ui.component.BrowserChrome
import net.buildabrowser.droided.ui.component.FrameGUI
import net.buildabrowser.droided.ui.theme.BuildABrowserDroidedTheme
import java.net.URI
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuildABrowserDroidedTheme {
                Surface(
                   color = MaterialTheme.colorScheme.background
                ) {
                    Browser(LocalContext.current, Modifier.safeDrawingPadding())
                }
            }
        }
    }
}

@Composable
fun Browser(context: Context, modifier: Modifier = Modifier) {
    val painter = remember { AndroidSkiaComposePainter() }
    val engine = remember { createRenderingEngine(context, painter) }
    BrowserTab(engine, modifier)
}

@Composable
fun BrowserTab(engine: RenderingEngine, modifier: Modifier = Modifier) {
    val frame = remember { createFrame(engine) }
    val frameGUI = remember { FrameGUI(frame) }

    Column (
        modifier = modifier
    ) {
        BrowserChrome(frame)
        frameGUI.FrameComponent(modifier)
    }
}

fun createFrame(engine: RenderingEngine) : Frame {
    val frame = engine.createFrame()
    frame.navigate(URI("https://buildabrowser.net/"))
    return frame
}

fun createRenderingEngine(context: Context, painter: Painter) : RenderingEngine {
    val fetchBackend = FetchBackendImp()
    val loaderRegistry = DocumentLoaderRegistry.createDefault()
    return RenderingEngine.create(
        fetchBackend,
        Executors::newWorkStealingPool,
        painter,
        loaderRegistry,
        context.assets::open
    )
}