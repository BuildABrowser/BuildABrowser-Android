package net.buildabrowser.droided.ui.input

import androidx.compose.ui.platform.SoftwareKeyboardController
import net.buildabrowser.babbrowser.renderer.api.VirtualKeyboard

class AndroidVirtualKeyboard(
    val keyboardController: SoftwareKeyboardController?
) : VirtualKeyboard {

    override fun show() {
        // TODO: Also need to request focus, but it's hard to get a handle
        // to the focus requestor because it's down the tree
        keyboardController?.show()
    }

    override fun close() {
        //keyboardController?.hide()
    }

}