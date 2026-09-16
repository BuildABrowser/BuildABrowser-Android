package net.buildabrowser.babbrowser.embedding.android.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.net.toUri
import net.buildabrowser.babbrowser.dom.Node
import net.buildabrowser.babbrowser.dom.util.HTMLSerializerUtil
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider
import java.io.InputStream
import java.net.URI

class AndroidClipboardProvider(
    private val clipboardManager: ClipboardManager,
    private val context: Context
) : ClipboardProvider<ClipData> {

    override fun createHtmlClip(
        node: Node,
        textFallback: String
    ): ClipData {
        val htmlText = HTMLSerializerUtil.serializeNode(node)
        return ClipData.newHtmlText("Copied HTML Data", textFallback, htmlText)
    }

    override fun createImageClip(
        imageURI: URI,
        imageBytesSupplier: ClipboardProvider.IOThrowingSupplier<InputStream>,
        altText: String
    ): ClipData {
        return ClipData.newUri(context.contentResolver, altText, imageURI.toString().toUri())
    }

    override fun setActiveClip(clip: ClipData) {
        clipboardManager.setPrimaryClip(clip)
    }

}