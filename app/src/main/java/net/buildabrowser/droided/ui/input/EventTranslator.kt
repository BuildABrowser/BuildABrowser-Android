package net.buildabrowser.droided.ui.input

import android.icu.lang.UCharacter.isPrintable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerKeyboardModifiers
import androidx.compose.ui.input.pointer.isAltPressed
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.isShiftPressed
import net.buildabrowser.babbrowser.dom.events.util.ModifierUtil
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent.KeyboardEventType
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent.MouseEventType
import net.buildabrowser.babbrowser.renderer.uistate.Frame

fun loopEvents(
    frame: Frame,
    scaling: Float,
    focusRequester: FocusRequester
): suspend AwaitPointerEventScope.() -> Unit = {
    var isDrag = false
    val touchSlop = viewConfiguration.touchSlop
    var startPosition: Offset? = null
    while (true) {
        val event = awaitPointerEvent()
        val change = event.changes.firstOrNull() ?: continue
        when (event.type) {
            PointerEventType.Press -> {
                isDrag = false
                startPosition = change.position
                focusRequester.requestFocus()
                handleGeneric(frame, event, MouseEventType.DOWN, scaling)
            }

            PointerEventType.Move -> {
                if (change.pressed) {
                    val start = startPosition
                    if (
                        start != null
                        && (change.position - start).getDistance() > touchSlop
                    ) {
                        isDrag = true
                    }
                }
                handleGeneric(frame, event, MouseEventType.MOVE, scaling)
            }

            PointerEventType.Release -> {
                focusRequester.requestFocus()
                if (!isDrag) {
                    handleGeneric(frame, event, MouseEventType.CLICK, scaling)
                }
                handleGeneric(frame, event, MouseEventType.UP, scaling)
            }
        }
    }
}

fun handleKeyEvent(
    frame: Frame,
    event: KeyEvent
): Boolean {
    val eventType = when (event.type) {
        KeyEventType.KeyDown -> KeyboardEventType.KEY_DOWN
        KeyEventType.KeyUp -> KeyboardEventType.KEY_UP
        else -> return false
    }

    fireKeyEvent(frame, eventType, event)
    if (
        event.type == KeyEventType.KeyDown
        && isPrintable(event.utf16CodePoint)
    ) {
        fireKeyEvent(frame, KeyboardEventType.KEY_PRESS, event)
    }

    return true
}

private fun fireKeyEvent(
    frame: Frame,
    type: KeyboardEventType,
    event: KeyEvent
) {
    val rendererEvent = remapEvent(type, event)
    val target = frame.renderer.eventForwardingTarget()
    target.forwardEvent(rendererEvent, EventHandlerResponse.UNHANDLED)
}

private fun remapEvent(type: KeyboardEventType, event: KeyEvent): RendererKeyboardEvent {
    val keyCode = when (event.key) {
        Key.Tab -> RendererKeyboardEvent.KEY_TAB
        Key.Enter, Key.NumPadEnter -> RendererKeyboardEvent.KEY_ENTER
        Key.Spacebar -> RendererKeyboardEvent.KEY_SPACE
        Key.Backspace -> RendererKeyboardEvent.KEY_BACKSPACE
        Key.DirectionLeft -> RendererKeyboardEvent.KEY_LEFT_ARROW
        Key.DirectionRight -> RendererKeyboardEvent.KEY_RIGHT_ARROW
        Key.DirectionUp -> RendererKeyboardEvent.KEY_UP_ARROW
        Key.DirectionDown -> RendererKeyboardEvent.KEY_DOWN_ARROW
        Key.MoveHome -> RendererKeyboardEvent.KEY_HOME
        Key.MoveEnd -> RendererKeyboardEvent.KEY_END
        Key.Delete -> RendererKeyboardEvent.KEY_DELETE
        Key.Insert -> RendererKeyboardEvent.KEY_INSERT
        Key.PageUp -> RendererKeyboardEvent.KEY_PAGE_UP
        Key.PageDown -> RendererKeyboardEvent.KEY_PAGE_DOWN
        Key.C -> RendererKeyboardEvent.KEY_C
        else -> RendererKeyboardEvent.KEY_UNIDENTIFIED
    }

    val modifiers = translateModifiers(event)
    val codePoint = event.utf16CodePoint
    val keyName = if (isPrintable(codePoint)) codePoint.toChar().toString() else ""
    val rawKeyCode = event.key.keyCode.toInt()

    return RendererKeyboardEvent(keyName, keyCode, rawKeyCode, modifiers, type)
}

private fun handleGeneric(
    frame: Frame,
    event: PointerEvent,
    type: MouseEventType,
    scaling: Float
) {
    val position = event.changes.first().position
    val buttons = 1; // event.buttons
    val modifiers = translateModifiers(event.keyboardModifiers)
    val mouseEvent = RendererMouseEvent.create(
        // TODO: Need to properly translate button
        position.x / scaling, position.y / scaling, buttons,
        type, modifiers)
    val target = frame.renderer.eventForwardingTarget()
    target.forwardEvent(mouseEvent, EventHandlerResponse.UNHANDLED)
}

private fun translateModifiers(e: PointerKeyboardModifiers): Byte {
    val modifiers = (
        (if (e.isAltPressed) ModifierUtil.MODIFIER_SHIFT else 0)
            + (if (e.isCtrlPressed) ModifierUtil.MODIFIER_CTRL else 0)
            + (if (e.isMetaPressed) ModifierUtil.MODIFIER_META else 0)
            + (if (e.isShiftPressed) ModifierUtil.MODIFIER_SHIFT else 0)
        ).toByte()

    // TODO: Check repeat
    return modifiers
}

private fun translateModifiers(e: KeyEvent): Byte {
    val modifiers = (
        (if (e.isAltPressed) ModifierUtil.MODIFIER_SHIFT else 0)
            + (if (e.isCtrlPressed) ModifierUtil.MODIFIER_CTRL else 0)
            + (if (e.isMetaPressed) ModifierUtil.MODIFIER_META else 0)
            + (if (e.isShiftPressed) ModifierUtil.MODIFIER_SHIFT else 0)
        ).toByte()

    // TODO: Check repeat
    return modifiers
}