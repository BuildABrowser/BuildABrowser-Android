package net.buildabrowser.droided.painter.androidskia

import net.buildabrowser.babbrowser.painter.core.LoadedFont
import net.buildabrowser.babbrowser.painter.core.Paint

class ASPaint : Paint {
    private var color = 0
    private var offsetX = 0f
    private var offsetY = 0f
    private var scaling = 1f
    private var selectedFont: ASLoadedFont = noFont()

    override fun setColor(color: Int) {
        this.color = color
    }

    override fun getColor() = this.color

    override fun setFont(font: LoadedFont) {
        require(font is ASLoadedFont) {
            "Attempt to pass non-android-skia font into Android Skia renderer!"
        }
        this.selectedFont = font
    }

    override fun getFont(): ASLoadedFont = this.selectedFont
}