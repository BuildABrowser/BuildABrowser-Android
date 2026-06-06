package net.buildabrowser.droided.painter.androidskia

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Picture
import net.buildabrowser.babbrowser.painter.core.PaintCanvas
import java.util.function.Consumer
import androidx.core.graphics.withTranslation

class ASCommandList(private val width: Int, private val height: Int) : ASPaintBitMap {
    private val picture: Picture = Picture()

    override fun withCanvas(paintFunc: Consumer<PaintCanvas>) {
        val canvas = picture.beginRecording(width, height)
        paintFunc.accept(ASPaintCanvas(canvas))
        picture.endRecording()
    }

    override fun draw(
        canvas: Canvas,
        paint: Paint?,
        x: Int,
        y: Int
    ) {
        canvas.withTranslation(x.toFloat(), y.toFloat()) {
            drawPicture(picture);
        }
    }
}
