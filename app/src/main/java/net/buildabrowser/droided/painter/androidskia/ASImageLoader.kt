package net.buildabrowser.droided.painter.androidskia

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import net.buildabrowser.babbrowser.common.util.BufferUtil
import net.buildabrowser.babbrowser.painter.core.ImageLoader
import net.buildabrowser.babbrowser.painter.core.LoadedImage
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class ASImageLoader(
    private val callbacks: ProgressiveImageCallbacks
) : ImageLoader {

    private val bufferOut = ByteArrayOutputStream()
    private var currentImage: LoadedImage? = null
    private var hasNewData = false
    private var bitmap: Bitmap? = null
    private var queuedException: Exception? = null
    private var done = false

    @Throws(IOException::class)
    override fun onChunk(chunk: ByteBuffer) {
        BufferUtil.writeBufferToStream(chunk, bufferOut)
        hasNewData = true
        callbacks.onImageUpdate()
    }

    @Throws(IOException::class)
    override fun onDone() {
        done = true
        if (hasNewData) {
            currentImage = loadCurrentImage()
        }

        if (queuedException != null) {
            callbacks.onImageFailure(queuedException)
        } else if (currentImage == null) {
            callbacks.onImageFailure(IOException("No image loaded!"))
        } else {
            callbacks.onImageDone()
            bufferOut.close()
        }
    }

    override fun onFailure(e: Exception?) {
        currentImage = null
        callbacks.onImageFailure(e)
        try {
            bufferOut.close()
        } catch (e2: IOException) {
            e2.printStackTrace()
        }
    }

    override fun currentImage(): LoadedImage? {
        if (hasNewData) {
            currentImage = loadCurrentImage()
            hasNewData = false
        }
        return currentImage
    }

    private fun loadCurrentImage(): LoadedImage? {
        val currentBytes = bufferOut.toByteArray()
        if (currentBytes.isEmpty()) return null

        try {
            val decoded = BitmapFactory.decodeByteArray(currentBytes, 0, currentBytes.size)

            if (decoded != null) {
                bitmap = decoded
                currentImage = ASLoadedImage(decoded)
                queuedException = null
            } else if (done) {
                throw IOException("Failed to decode image from byte buffer.")
            }
        } catch (e: Exception) {
            queuedException = e
            currentImage = null
            if (done) {
                onFailure(e)
            }
        }

        return currentImage
    }
}