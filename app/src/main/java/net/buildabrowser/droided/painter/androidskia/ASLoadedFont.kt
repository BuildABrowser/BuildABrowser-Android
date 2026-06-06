package net.buildabrowser.droided.painter.androidskia

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions
import net.buildabrowser.babbrowser.painter.core.LoadedFont

class ASLoadedFont(
    private val rawFonts: Array<Typeface>,
    private val fontOptions: FontOptions
) : LoadedFont {
    private val fontPaint: Paint = Paint() // TODO: Re-use this for the metrics?
    private val metrics = ASFontMetrics(rawFonts, fontOptions)
    private val effectiveFontCache = arrayOfNulls<Typeface>(256)

    override fun metrics() = metrics

    fun drawText(
        x: Float, y: Float, text: String,
        canvas: Canvas, rawPaint: Paint
    ) {
        if (text.isEmpty()) return
        rawPaint.textSize = fontOptions.size

        var windowStart = 0
        var currentX = x
        val adjustedY = y - metrics.ascent()
        while (windowStart < text.length) {
            val currentFont = glyphFont(text.codePointAt(windowStart))
            val windowEnd = endOfConsecutiveFontChars(text, windowStart)
            val windowText = text.substring(windowStart, windowEnd + 1)

            currentX += drawPartialText(
                windowText, currentX, adjustedY, currentFont ?: rawPaint.typeface,
                canvas, rawPaint
            )
            windowStart = windowEnd + 1
        }
    }

    private fun drawPartialText(
        text: String, x: Float, y: Float, font: Typeface,
        canvas: Canvas, rawPaint: Paint
    ): Float {
        rawPaint.typeface = font
        // TODO: Add letter spacing
        canvas.drawText(text, x, y, rawPaint)
        return rawPaint.measureText(text)
    }

    private fun endOfConsecutiveFontChars(text: String, windowStart: Int): Int {
        val initialFont = glyphFont(text.codePointAt(windowStart))
        var windowEnd = windowStart + 1
        while (windowEnd < text.length) {
            val font = glyphFont(text.codePointAt(windowEnd))
            if (font !== initialFont) {
                return windowEnd - 1
            }
            windowEnd++
        }

        return windowEnd - 1
    }

    private fun glyphFont(codePoint: Int): Typeface? {
        if (rawFonts.isEmpty()) return null

        // TODO: Look into caching codepoints outside of this, for better i18n performance
        if (
            codePoint < 256
            && effectiveFontCache[codePoint] != null
        ) {
            return checkNotNull(effectiveFontCache[codePoint])
        }

        val rawFont = glyphFontRaw(codePoint)
        if (codePoint < 256) {
            effectiveFontCache[codePoint] = rawFont
        }

        return rawFont
    }

    private fun glyphFontRaw(codePoint: Int): Typeface {
        val glyphName = String(Character.toChars(codePoint))
        for (rawFont in rawFonts) {
            fontPaint.typeface = rawFont
            if (fontPaint.hasGlyph(glyphName)) {
                return  rawFont
            }
        }

        // TODO: Scan all system fonts
        return rawFonts[0]
    }
}

fun noFont(): ASLoadedFont {
    return ASLoadedFont(
        emptyArray(),
        FontOptions(emptyList(), 12f, 500))
}
