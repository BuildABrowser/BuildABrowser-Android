package net.buildabrowser.droided.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.buildabrowser.babbrowser.renderer.uistate.Frame
import net.buildabrowser.babbrowser.renderer.uistate.event.FrameEventListener
import java.util.UUID

@Composable
fun DecorTabBar(
    frames: SnapshotStateMap<UUID, Frame>,
    activeFrameId: UUID,
    tabOrder: SnapshotStateList<UUID>,
    onTabClose: (UUID) -> Unit,
    onTabSelection: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    onNewTab: () -> Unit
) {
    Row (modifier) {
        TabBar(
            frames, activeFrameId, tabOrder, onTabClose, onTabSelection,
            modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier
                .padding(0.dp, 0.dp, 4.dp, 3.dp),
            onClick = { onNewTab() }
        ) {
            Icon(Icons.Filled.Add, contentDescription = "New Tab")
        }
    }
}

@Composable
fun TabBar(
    frames: SnapshotStateMap<UUID, Frame>,
    activeFrameId: UUID,
    tabOrder: SnapshotStateList<UUID>,
    onTabClose: (UUID) -> Unit,
    onTabSelection: (UUID) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeIndex = tabOrder.indexOf(activeFrameId)
    PrimaryScrollableTabRow(
        modifier = modifier,
        selectedTabIndex = activeIndex,
        edgePadding = 0.dp,
        divider = {},
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(activeIndex),
                // TODO: Maybe use the secondary color, but it's the same as the text color on my device...
                color = Color(66, 135, 245), // TODO: Also move this to a resource file
                height = 3.dp
            )
        }
    ) {
        tabOrder.forEach { tabId ->
            PageTab(
                frame = frames[tabId]!!,
                onTabClose = onTabClose,
                onTabSelection = onTabSelection,
                selected = tabId == activeFrameId,
                tabId = tabId
            )
        }
    }
}

@Composable
fun PageTab(
    frame: Frame,
    onTabClose: (UUID) -> Unit,
    onTabSelection: (UUID) -> Unit,
    selected: Boolean,
    tabId: UUID
) {
    val shownTitle = remember { mutableStateOf("Untitled Document") }

    DisposableEffect(frame.renderer) {
        val listener = TabListener(shownTitle)
        frame.addEventListener(listener, true)
        // TODO: Also need to add a method to remove the event listener
        onDispose {  }
    }

    Tab(
        modifier = Modifier
            .height(40.dp),
        selected = selected,
        onClick = { onTabSelection(tabId) },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.width(128.dp),
                    text = shownTitle.value,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp
                )
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = { onTabClose(tabId) }
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close Tab")
                }
            }
        }
    )
}

private class TabListener(
    private val shownTitle: MutableState<String>
) : FrameEventListener {

    override fun onTitleChange(title: String) {
        shownTitle.value = title
    }

}