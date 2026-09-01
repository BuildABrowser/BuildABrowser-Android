package net.buildabrowser.droided.painter.androidskia

import net.buildabrowser.babbrowser.painter.core.LoadedFont
import net.buildabrowser.babbrowser.painter.core.Paint

class ASPaint : Paint {
    private var color = 0
    private var selectedFont: ASLoadedFont = noFont()
    private var filled = true
    private var strokeSize = 1f

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

    override fun setFilled(filled: Boolean) {
        this.filled = filled
    }

    override fun getFilled() = this.filled

    override fun setStrokeSize(strokeSize: Float) {
        this.strokeSize = strokeSize
    }

    override fun getStrokeSize() = this.strokeSize
}