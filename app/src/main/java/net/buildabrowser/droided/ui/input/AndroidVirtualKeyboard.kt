package net.buildabrowser.droided.ui.input

import android.text.InputType
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.platform.PlatformTextInputModifierNode
import androidx.compose.ui.platform.establishTextInputSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.buildabrowser.babbrowser.renderer.content.input.VirtualKeyboard
import net.buildabrowser.babbrowser.renderer.input.OffThreadWriteTextController
import net.buildabrowser.babbrowser.renderer.uistate.Frame

class AndroidVirtualKeyboard(
    private val frame: Frame
) : VirtualKeyboard {

    private var activeConnection: TextControllerInputConnection? = null
    private var textInputNode: PlatformTextInputModifierNode? = null
    private var scope: CoroutineScope? = null
    private var sessionJob: Job? = null

    internal fun bind(node: PlatformTextInputModifierNode, coroutineScope: CoroutineScope) {
        this.textInputNode = node
        this.scope = coroutineScope
    }

    internal fun unbind() {
        close()
        this.textInputNode = null
        this.scope = null
    }

    override fun onInputConnected(textController: OffThreadWriteTextController) {
        activeConnection = TextControllerInputConnection(textController, frame)
    }

    override fun show() {
        val node = textInputNode ?: return
        val coroutineScope = scope ?: return
        val connection = activeConnection ?: return

        sessionJob?.cancel()
        sessionJob = coroutineScope.launch {
            node.establishTextInputSession {
                startInputMethod { outAttrs ->
                    outAttrs.inputType = InputType.TYPE_CLASS_TEXT
                    outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE
                    connection
                }
            }
        }
    }

    override fun close() {
        sessionJob?.cancel()
        sessionJob = null
    }

}