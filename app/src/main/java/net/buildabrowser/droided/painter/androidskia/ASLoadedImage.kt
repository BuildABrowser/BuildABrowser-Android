package net.buildabrowser.droided.painter.androidskia

import android.graphics.Bitmap
import net.buildabrowser.babbrowser.painter.core.LoadedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream


@JvmInline
value class ASLoadedImage(val bitmap: Bitmap) : LoadedImage {
    override fun width() = bitmap.width
    override fun height() = bitmap.height

    override fun streamData(): InputStream {
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, output)
        val bitmapData = output.toByteArray()

        return ByteArrayInputStream(bitmapData)
    }
}
