package net.buildabrowser.droided

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
import net.buildabrowser.droided.ui.component.FrameGUI
import net.buildabrowser.droided.ui.theme.BuildABrowserDroidedTheme

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
                ) { innerPadding -> Browser(Modifier.padding(innerPadding)) }
            }
        }
    }
}

@Composable
fun Browser(modifier: Modifier = Modifier) {
    val frameGUI = remember { FrameGUI() }
    frameGUI.FrameComponent(modifier)
}