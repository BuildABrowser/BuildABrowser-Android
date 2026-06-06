package net.buildabrowser.droided.painter.androidskia

import android.graphics.Canvas
import android.graphics.Paint
import net.buildabrowser.babbrowser.painter.core.PaintBitMap

interface ASPaintBitMap : PaintBitMap {
    fun draw(canvas: Canvas, paint: Paint?, x: Int, y: Int)
}