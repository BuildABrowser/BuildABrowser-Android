package net.buildabrowser.droided.painter.androidskia

import net.buildabrowser.babbrowser.painter.core.PaintBitMap
import net.buildabrowser.babbrowser.painter.core.Painter
import net.buildabrowser.babbrowser.painter.core.ResourceLoader

open class AndroidSkiaPainter : Painter {
    override fun resourceLoader(): ResourceLoader? {
        TODO("Not yet implemented")
    }

    override fun createPaintBitMap(
        width: Int,
        height: Int
    ): PaintBitMap {
        return ASCommandList(width, height)
    }
}