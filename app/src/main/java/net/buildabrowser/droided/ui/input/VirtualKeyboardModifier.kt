package net.buildabrowser.droided.ui.input

import androidx.compose.ui.Modifier
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.PlatformTextInputModifierNode

private class VirtualKeyboardModifierNode(
    var controller: AndroidVirtualKeyboard
) : Modifier.Node(), PlatformTextInputModifierNode {

    override fun onAttach() {
        super.onAttach()
        controller.bind(this, coroutineScope)
    }

    override fun onDetach() {
        controller.unbind()
        super.onDetach()
    }
}

private data class VirtualKeyboardElement(
    val controller: AndroidVirtualKeyboard
) : ModifierNodeElement<VirtualKeyboardModifierNode>() {

    override fun create(): VirtualKeyboardModifierNode =
        VirtualKeyboardModifierNode(controller)

    override fun update(node: VirtualKeyboardModifierNode) {
        node.controller.unbind()
        node.controller = controller
        node.controller.bind(node, node.coroutineScope)
    }
}

fun Modifier.virtualKeyboardInput(
    controller: AndroidVirtualKeyboard
): Modifier = this then VirtualKeyboardElement(controller)