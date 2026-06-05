package net.buildabrowser.droided.painter.androidskia

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks

class AndroidSkiaComposePainter : AndroidSkiaPainter() {

    @Composable
    fun PainterCanvas(callbacks: CanvasCallbacks, modifier: Modifier = Modifier) {
        Canvas(modifier = modifier.fillMaxSize()) {
            callbacks.layout(size.width, size.height)
            drawIntoCanvas { canvas ->
                val graphics = canvas.nativeCanvas
                val wrapper = ASPaintCanvas(graphics)
                callbacks.paint(wrapper);
            }
        }
    }

}