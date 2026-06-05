package net.buildabrowser.droided.painter.androidskia

import android.graphics.Picture
import net.buildabrowser.babbrowser.painter.core.PaintCanvas
import java.util.function.Consumer

class ASCommandList(private val width: Int, private val height: Int) : ASPaintBitMap {
    private val picture: Picture = Picture()

    override fun withCanvas(paintFunc: Consumer<PaintCanvas>) {
        val canvas = picture.beginRecording(width, height)
        paintFunc.accept(ASPaintCanvas(canvas))
        picture.endRecording()
    }
}
