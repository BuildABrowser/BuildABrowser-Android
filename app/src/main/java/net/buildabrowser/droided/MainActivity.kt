package net.buildabrowser.droided

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import net.buildabrowser.babbrowser.painter.core.Painter
import net.buildabrowser.babbrowser.renderer.RenderingEngine
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry
import net.buildabrowser.babbrowser.renderer.uistate.Frame
import net.buildabrowser.droided.network.imp.FetchBackendImp
import net.buildabrowser.droided.painter.androidskia.AndroidSkiaComposePainter
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
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                ) { innerPadding -> Browser(this, Modifier.padding(innerPadding)) }
            }
        }
    }
}

@Composable
fun Browser(context: Context, modifier: Modifier = Modifier) {
    val painter = remember { AndroidSkiaComposePainter() }
    val engine = remember { createRenderingEngine(context, painter) }
    val frame = remember { createFrame(engine) }
    val frameGUI = remember { FrameGUI(frame) }
    frameGUI.FrameComponent(modifier)
}

fun createFrame(engine: RenderingEngine) : Frame {
    val frame = engine.createFrame()
    frame.navigate(URI("https://registry.khronos.org/vulkan/specs/latest/html/vkspec.html"))
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