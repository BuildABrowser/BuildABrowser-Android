package net.buildabrowser.droided.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks
import net.buildabrowser.babbrowser.painter.core.PaintCanvas
import net.buildabrowser.droided.painter.androidskia.AndroidSkiaComposePainter

class FrameGUI {

    class FrameCallbacks(private val scaling: Float) : CanvasCallbacks {
        override fun layout(width: Float, height: Float) {

        }

        override fun paint(canvas: PaintCanvas) {
            canvas.withPaintAndTransform(
                { it.color = 0x77777777 },
                { it.scale(scaling, scaling) },
                {
                    it.drawBox(10f, 10f, 100f, 100f)
                    it.drawText(20f, 20f, "HELLO WORLD!")
                }
            )
        }
    }

    @Composable
    fun FrameComponent(modifier: Modifier = Modifier) {
        val scaling = LocalDensity.current.density
        val painter = remember { AndroidSkiaComposePainter() }
        val callbacks = remember { FrameCallbacks(scaling * 160 / 96) }
        painter.PainterCanvas(callbacks, modifier)
    }

}