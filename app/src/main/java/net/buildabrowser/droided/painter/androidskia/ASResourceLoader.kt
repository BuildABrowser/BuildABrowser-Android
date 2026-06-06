package net.buildabrowser.droided.painter.androidskia

import android.graphics.BitmapFactory
import net.buildabrowser.babbrowser.painter.core.FontLoader
import net.buildabrowser.babbrowser.painter.core.LoadedImage
import net.buildabrowser.babbrowser.painter.core.ResourceLoader
import java.io.InputStream

class ASResourceLoader : ResourceLoader {

    override fun loadImage(imageStream: InputStream): LoadedImage {
        return ASLoadedImage(BitmapFactory.decodeStream(imageStream))
    }

    override fun fontLoader(): FontLoader {
        return ASFontLoader()
    }

}
