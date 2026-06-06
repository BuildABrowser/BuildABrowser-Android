package net.buildabrowser.droided.painter.androidskia

import android.graphics.Typeface
import net.buildabrowser.babbrowser.painter.core.FontLoader
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontFamily
import net.buildabrowser.babbrowser.painter.core.FontLoader.FontOptions
import net.buildabrowser.babbrowser.painter.core.LoadedFont

class ASFontLoader : FontLoader {

    override fun monospace() = ASFontFamily(Typeface.MONOSPACE)
    override fun serif() = ASFontFamily(Typeface.SERIF)
    override fun sansSerif() = ASFontFamily(Typeface.SANS_SERIF)

    override fun named(name: String) = ASFontFamily(
        Typeface.create(name, Typeface.NORMAL))

    override fun load(options: FontOptions): LoadedFont {
        val finTypeFaces = options.families().map {
            require (it is ASFontFamily) {
                "Passed font-family must have been loaded via ASFontLoader!"
            }
            Typeface.create(it.getTypeFace(), options.weight, false)
        }

        return ASLoadedFont(finTypeFaces.toTypedArray(), options)
    }

    class ASFontFamily(private val typeface: Typeface) : FontFamily {

        fun getTypeFace() = typeface

    }

}
