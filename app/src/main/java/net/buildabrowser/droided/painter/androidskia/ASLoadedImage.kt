package net.buildabrowser.droided.painter.androidskia

import android.graphics.Bitmap
import net.buildabrowser.babbrowser.painter.core.LoadedImage

@JvmInline
value class ASLoadedImage(val bitmap: Bitmap) : LoadedImage {
    override fun width() = bitmap.width
    override fun height() = bitmap.height
}
