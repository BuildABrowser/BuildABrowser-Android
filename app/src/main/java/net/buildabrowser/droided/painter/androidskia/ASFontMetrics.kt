package net.buildabrowser.droided.painter.androidskia

import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions
import net.buildabrowser.babbrowser.painter.core.FontMetrics
import android.graphics.Paint.FontMetrics as AndroidFontMetrics


class ASFontMetrics(
    private val rawFonts: Array<Typeface>,
    private val fontOptions: FontOptions
) : FontMetrics {
    private val fontPaint: Paint = Paint()
    private val primaryMetrics: AndroidFontMetrics
    private val widthCache = FloatArray(256)
    private val xHeight: Float


    init {
        fontPaint.textSize = fontOptions.size
        if (!rawFonts.isEmpty()) {
            fontPaint.typeface = rawFonts[0]
        }

        this.primaryMetrics = fontPaint.fontMetrics

        val xMeasure = Rect()
        fontPaint.getTextBounds("x", 0, 1, xMeasure)
        this.xHeight = xMeasure.height().toFloat()
    }

    override fun size() = fontOptions.size
    override fun weight() = fontOptions.weight
    override fun height() = primaryMetrics.descent - primaryMetrics.ascent + primaryMetrics.leading
    override fun xHeight() = xHeight
    override fun ascent() = primaryMetrics.ascent
    override fun descent() = primaryMetrics.descent

    // TODO: Group contiguous font runs so we can check an entire string at once
    override fun stringWidth(text: String): Float {
        var textWidth = 0f
        var i = 0
        while (i < text.length) {
            val codePoint = text.codePointAt(i)
            textWidth += getCharacterWidth(codePoint)
            i += Character.charCount(codePoint)
        }

        return textWidth
    }

    private fun getCharacterWidth(codePoint: Int): Float {
        if (codePoint < 256 && widthCache[codePoint] != 0f) {
            return widthCache[codePoint]
        }

        val glyphName = String(Character.toChars(codePoint))
        fontPaint.typeface = rawFonts[0]
        var width: Float = fontPaint.measureText(glyphName)
        for (rawFont in rawFonts) {
            fontPaint.typeface = rawFont
            if (fontPaint.hasGlyph(glyphName)) {
                width = fontPaint.measureText(glyphName)
                break
            }
        }

        if (codePoint < 256) {
            widthCache[codePoint] = width
        }

        return width
    }
}
