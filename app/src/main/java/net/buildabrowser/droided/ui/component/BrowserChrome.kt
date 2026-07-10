package net.buildabrowser.droided.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.buildabrowser.babbrowser.renderer.uistate.Frame

@Composable
fun BrowserChrome(frame: Frame) {
    BoxWithConstraints {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp, 8.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (this@BoxWithConstraints.maxWidth >= 800.dp) {
                NavRow(frame)
            }
            URLField({ frame.navigate(it) }, modifier = Modifier.weight(1f))
            OptionsMenu(frame)
        }
    }
}

@Composable
fun OptionsMenu(frame: Frame) {
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