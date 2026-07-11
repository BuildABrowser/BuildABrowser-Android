package net.buildabrowser.droided.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Consumer
import net.buildabrowser.babbrowser.common.util.CommonUtil
import net.buildabrowser.babbrowser.common.util.CommonUtil.tryOrNull
import net.buildabrowser.babbrowser.network.URLUtil
import net.buildabrowser.babbrowser.renderer.uistate.Frame
import net.buildabrowser.babbrowser.renderer.uistate.event.FrameEventListener
import java.net.URI
import java.net.URLEncoder

private const val SEARCH_QUERY = "https://html.duckduckgo.com/html/?q=%s"
// TODO: Share the same constant as the code to make a new tab
// TODO: Better yet, detect if we're on the initial page (as the user may navigate here later)
private const val DEFAULT_TAB = "https://buildabrowser.net/"

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun URLField(
    frame: Frame,
    onNavigate: Consumer<URI>,
    modifier: Modifier = Modifier
) {
    val textState = rememberTextFieldState()
    val colors = SearchBarDefaults.colors()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    DisposableEffect(frame.renderer) {
        val listener = URLListener(textState, focusManager)
        frame.addEventListener(listener, true)
        // TODO: Also need to add a method to remove the event listener
        onDispose {  }
    }

    // Can't use SearchBar, it has a minimum height
    BasicTextField(
        modifier = modifier
            .height(40.dp)
            .background(
                colors.containerColor,
                RoundedCornerShape(20.dp)
            ),
        textStyle = TextStyle(
            color = colors.inputFieldColors.focusedTextColor,
            fontSize = 16.sp),
        cursorBrush = SolidColor(colors.inputFieldColors.focusedTextColor),
        value = textState.text.toString(),
        onValueChange = { textState.edit { replace(0, length, it) } },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                val url = searchToURL(textState.text.toString())
                onNavigate.accept(url)
                keyboardController?.hide()
            }
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (textState.text.isEmpty()) {
                    Text(
                        "Search or enter a URL...",
                        color = colors.inputFieldColors.focusedPlaceholderColor,
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

private fun searchToURL(urlText: String): URI {
    var uri: URI? = tryOrNull {
        URLUtil.createURL( // TODO: Fallback to http if https not supported
            if (urlText.contains(":")) urlText else "https://$urlText"
        )
    }

    val couldBeDataURL =
        urlText.startsWith("data:")
                && urlText.indexOf('/') < urlText.indexOf(',')
                && urlText.indexOf('/') != -1
    if (
        uri == null
        || !(urlText.contains(".") || couldBeDataURL)
        || urlText.contains(" ")
    ) {
        val searchQuery = URLEncoder.encode(urlText, "UTF-8")
        val searchURL: String = String.format(SEARCH_QUERY, searchQuery)
        uri = CommonUtil.rethrow { URLUtil.createURL(searchURL) }
    }

    return uri
}

private class URLListener(
    private val shownURL: TextFieldState,
    private val focusManager: FocusManager
) : FrameEventListener {

    override fun onURLChange(url: URI) {
        val newText = if (url.toString() != DEFAULT_TAB) url.toString() else ""
        shownURL.edit { replace(0, length, newText) }
        focusManager.clearFocus()
    }

}