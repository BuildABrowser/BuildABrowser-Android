package net.buildabrowser.droided.ui.component

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks
import net.buildabrowser.babbrowser.painter.core.PaintCanvas
import net.buildabrowser.babbrowser.renderer.uistate.Frame
import net.buildabrowser.droided.painter.androidskia.AndroidSkiaComposePainter
import net.buildabrowser.droided.ui.input.AndroidVirtualKeyboard
import net.buildabrowser.droided.ui.input.handleKeyEvent
import net.buildabrowser.droided.ui.input.loopEvents
import net.buildabrowser.droided.ui.input.virtualKeyboardInput


class FrameGUI(val frame: Frame) {

    inner class FrameCallbacks(private val scaling: Float) : CanvasCallbacks {
        private var width = 0f
        private var height = 0f

        override fun layout(width: Float, height: Float) {
            this.width = width
            this.height = height
        }

        override fun paint(canvas: PaintCanvas) {
            frame.renderer.resize(
                (width / scaling).toInt(),
                (height / scaling).toInt())
            canvas.withPaint({ it.color = 0xFFFFFFFF.toInt() }) {
                it.drawBox(0f, 0f, width, height)
            }
            canvas.withTransform(
                { it.scale(scaling, scaling) },
                {
                    frame.renderer.draw(it)
                }
            )
        }
    }

    @Composable
    fun FrameComponent(modifier: Modifier = Modifier) {
        val scaling = 160f / 96f
        val painter = remember { AndroidSkiaComposePainter() }
        val callbacks = remember { FrameCallbacks(scaling) }
        var repaintTick by remember { mutableIntStateOf(0) }
        val focusRequester = remember { FocusRequester() }
        val virtualKeyboard = frame.frameAPIs().virtualKeyboard()

        DisposableEffect(frame.renderer) {
            val listener = Runnable {
                repaintTick++
            }
            frame.addRepaintListener(listener)

            onDispose {
                frame.removeRepaintListener(listener)
            }
        }

        painter.PainterCanvas(
            callbacks, { repaintTick },
            modifier = modifier
                .pointerInput(Unit) {
                    awaitPointerEventScope(loopEvents(frame, scaling, focusRequester))
                }
                .onKeyEvent { handleKeyEvent(frame, it) }
                .focusRequester(focusRequester)
                .focusable()
                .virtualKeyboardInput(virtualKeyboard as AndroidVirtualKeyboard))
    }

}