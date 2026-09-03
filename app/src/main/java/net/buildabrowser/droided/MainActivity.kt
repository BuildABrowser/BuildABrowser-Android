package net.buildabrowser.droided

import android.app.Activity
import android.content.ClipboardManager
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import net.buildabrowser.babbrowser.cookies.stores.InMemoryCookieStore
import net.buildabrowser.babbrowser.fetch.FetchConfig
import net.buildabrowser.babbrowser.fetch.FetchPolicy
import net.buildabrowser.babbrowser.painter.core.Painter
import net.buildabrowser.babbrowser.renderer.RenderingEngine
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry
import net.buildabrowser.babbrowser.renderer.uistate.Frame
import net.buildabrowser.droided.network.imp.FetchBackendImp
import net.buildabrowser.droided.painter.androidskia.AndroidSkiaComposePainter
import net.buildabrowser.droided.ui.AndroidClipboardProvider
import net.buildabrowser.droided.ui.AndroidUAUIFeatures
import net.buildabrowser.droided.ui.input.AndroidVirtualKeyboard
import net.buildabrowser.droided.ui.component.BrowserChrome
import net.buildabrowser.droided.ui.component.FrameGUI
import net.buildabrowser.droided.ui.theme.BuildABrowserDroidedTheme
import java.net.URI
import java.util.UUID
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
                    Browser(this, Modifier.safeDrawingPadding())
                }
            }
        }
    }
}

@Composable
fun Browser(activity: Activity, modifier: Modifier = Modifier) {
    val painter = remember { AndroidSkiaComposePainter() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val engine = remember { createRenderingEngine(
        activity, painter, keyboardController) }

    val frames: SnapshotStateMap<UUID, Frame> = remember { mutableStateMapOf() }
    val frameGUIs: SnapshotStateMap<UUID, FrameGUI> = remember { mutableStateMapOf() }
    val activeFrameId: MutableState<UUID?> = remember { mutableStateOf(null) }
    val tabOrder: SnapshotStateList<UUID> = remember { mutableStateListOf() }

    val uiState = UIState(engine, frames, frameGUIs, activeFrameId, tabOrder)


    LaunchedEffect(Unit) {
        createAndFocusTab(uiState)
    }

    val frameId = activeFrameId.value
    if (frameId != null) Column (
        modifier = modifier
    ) {
        BrowserChrome(
            frames = frames,
            activeFrameId = frameId,
            tabOrder = tabOrder,
            onNewTab = { createAndFocusTab(uiState) },
            onTabSelection = { activeFrameId.value = it },
            onTabClose = { closeTabById(it, uiState) },
            onAppExit = { println("Run"); activity.finishAndRemoveTask() }
        )

        key (frameId) {
            frameGUIs[frameId]!!.FrameComponent(modifier)
        }
    }
}

private fun createAndFocusTab(uiState: UIState) {
    val tabId = UUID.randomUUID()
    val newFrame = createFrame(uiState.engine)
    uiState.frames[tabId] = newFrame
    val frameGUI = FrameGUI(newFrame)
    uiState.frameGUIs[tabId] = frameGUI
    uiState.tabOrder.add(tabId)
    uiState.activeFrameId.value = tabId
}

private fun closeTabById(
    tabId: UUID,
    uiState: UIState
) {
    uiState.frames.remove(tabId)?.close()
    uiState.frameGUIs.remove(tabId)
    if (uiState.frames.isEmpty()) {
        createAndFocusTab(uiState)
    }

    val oldIndex = uiState.tabOrder.indexOf(tabId)
    uiState.tabOrder.remove(tabId)
    if (uiState.activeFrameId.value == tabId) {
        val newIndex = if (oldIndex < uiState.tabOrder.size)
            oldIndex
            else uiState.tabOrder.size - 1
        uiState.activeFrameId.value = uiState.tabOrder[newIndex]
    }
}

private fun createFrame(engine: RenderingEngine) : Frame {
    val frame = engine.createFrame()
    frame.navigate(URI("https://buildabrowser.net/"))
    return frame
}

private fun createRenderingEngine(
    context: Context,
    painter: Painter,
    keyboardController: SoftwareKeyboardController?
) : RenderingEngine {
    val fetchBackend = FetchBackendImp()
    val loaderRegistry = DocumentLoaderRegistry.createDefault()
    val fetchConfig = FetchConfig(
        fetchBackend,
        object : FetchPolicy {},
        InMemoryCookieStore { false })
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipboardProvider = AndroidClipboardProvider(clipboard, context)
    val virtualKeyboard = AndroidVirtualKeyboard(keyboardController)
    val uaUIFeatures = AndroidUAUIFeatures()
    return RenderingEngine.create(
        fetchConfig,
        Executors::newWorkStealingPool,
        painter,
        loaderRegistry,
        context.assets::open,
        clipboardProvider,
        virtualKeyboard,
        uaUIFeatures
    )
}

private class UIState(
    val engine: RenderingEngine,
    val frames: SnapshotStateMap<UUID, Frame>,
    val frameGUIs: SnapshotStateMap<UUID, FrameGUI>,
    val activeFrameId: MutableState<UUID?>,
    // Unfortunately can't be set, we need get and indexOf
    val tabOrder: SnapshotStateList<UUID>
)