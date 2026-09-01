package net.buildabrowser.droided.painter.androidskia

import android.graphics.BitmapFactory
import net.buildabrowser.babbrowser.painter.core.FontLoader
import net.buildabrowser.babbrowser.painter.core.ImageLoader
import net.buildabrowser.babbrowser.painter.core.LoadedImage
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks
import net.buildabrowser.babbrowser.painter.core.ResourceLoader
import java.io.InputStream
import java.util.function.Consumer

class ASResourceLoader : ResourceLoader {

    override fun loadImage(imageStream: InputStream): LoadedImage {
        return ASLoadedImage(BitmapFactory.decodeStream(imageStream))
    }

    override fun progressivelyLoadImage(
        mimeType: String,
        callbacks: ProgressiveImageCallbacks,
        threadRunner: Consumer<Runnable>
    ): ImageLoader {
        return ASImageLoader(callbacks)
    }

    override fun fontLoader(): FontLoader {
        return ASFontLoader()
    }

}
