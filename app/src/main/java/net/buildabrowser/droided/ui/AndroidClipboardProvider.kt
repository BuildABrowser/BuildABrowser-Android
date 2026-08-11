package net.buildabrowser.droided.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import net.buildabrowser.babbrowser.dom.Node
import net.buildabrowser.babbrowser.dom.util.HTMLSerializerUtil
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider.IOThrowingSupplier
import java.io.InputStream
import java.net.URI
import androidx.core.net.toUri

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
        imageBytesSupplier: IOThrowingSupplier<InputStream>,
        altText: String
    ): ClipData {
        return ClipData.newUri(context.contentResolver, altText, imageURI.toString().toUri())
    }

    override fun setActiveClip(clip: ClipData) {
        clipboardManager.setPrimaryClip(clip)
    }

}