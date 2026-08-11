package net.buildabrowser.droided.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.buildabrowser.babbrowser.renderer.uistate.Frame
import java.util.UUID

@Composable
fun BrowserChrome(
    frames: SnapshotStateMap<UUID, Frame>,
    activeFrameId: UUID,
    tabOrder: SnapshotStateList<UUID>,
    onTabClose: (UUID) -> Unit,
    onTabSelection: (UUID) -> Unit,
    onNewTab: () -> Unit,
    onAppExit: () -> Unit
) {
    val activeFrame = frames[activeFrameId]!!
    Column {
        DecorTabBar(
            modifier = Modifier
                .padding(8.dp, 0.dp, 4.dp, 8.dp)
                .height(40.dp),
            frames = frames,
            activeFrameId = activeFrameId,
            tabOrder = tabOrder,
            onTabClose = onTabClose,
            onTabSelection = onTabSelection,
            onNewTab = onNewTab
        )
        val saveableStateHolder = rememberSaveableStateHolder()
        saveableStateHolder.SaveableStateProvider (activeFrameId) {
            BoxWithConstraints {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 4.dp, 8.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (this@BoxWithConstraints.maxWidth >= 800.dp) {
                        NavRow(activeFrame)
                    }
                    URLField(
                        activeFrame,
                        onNavigate = { activeFrame.navigate(it) },
                        modifier = Modifier.weight(1f)
                    )
                    OptionsMenu(
                        frame = activeFrame,
                        tabId = activeFrameId,
                        onNewTab = onNewTab,
                        onTabClose = onTabClose,
                        onAppExit = onAppExit
                    )
                }
            }
        }
    }
}

@Composable
fun OptionsMenu(
    frame: Frame,
    tabId: UUID,
    onNewTab: () -> Unit,
    onTabClose: (UUID) -> Unit,
    onAppExit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More options")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            NavRow(frame)
            DropdownMenuItem(
                text = { Text("New Tab") },
                onClick = { onNewTab() }
            )
            DropdownMenuItem(
                text = { Text("Close Tab") },
                onClick = { onTabClose(tabId) }
            )
            DropdownMenuItem(
                text = { Text("Exit") },
                onClick = { onAppExit() }
            )
        }
    }
}

@Composable
fun NavRow(frame: Frame) {
    Row {
        IconButton(onClick = { frame.back() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        IconButton(onClick = { frame.reload() }) {
            Icon(Icons.Default.Refresh, contentDescription = "Reload")
        }
        IconButton(onClick = { frame.forward() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Forward")
        }
    }
}

