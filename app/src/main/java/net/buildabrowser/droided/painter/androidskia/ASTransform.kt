package net.buildabrowser.droided.painter.androidskia

import android.graphics.Canvas
import net.buildabrowser.babbrowser.painter.core.Transform

class ASTransform(private val canvas: Canvas) : Transform {

    override fun translate(x: Float, y: Float) {
        canvas.translate(x, y)
    }

    override fun scale(scalingX: Float, scalingY: Float) {
        canvas.scale(scalingX, scalingY)
    }

}