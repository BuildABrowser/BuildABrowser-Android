package net.buildabrowser.droided.ui.input

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.inputmethod.CompletionInfo
import android.view.inputmethod.CorrectionInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputContentInfo
import net.buildabrowser.babbrowser.renderer.input.OffThreadWriteTextController
import net.buildabrowser.babbrowser.renderer.uistate.Frame
import androidx.compose.ui.input.key.KeyEvent as ComposeKeyEvent

class TextControllerInputConnection(
    private val textController: OffThreadWriteTextController,
    private val frame: Frame
) : InputConnection {

    var isOpen = true

    override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
        if (!isOpen) return false

        textController.insertText(text.toString())
        if (newCursorPosition > 0) {
            textController.moveCursorForward(newCursorPosition - 1)
        } else {
            textController.moveCursorForward(-newCursorPosition - text.length)
        }
        return true
    }

    override fun sendKeyEvent(event: KeyEvent): Boolean {
        if (!isOpen) return false

        handleKeyEvent(frame, ComposeKeyEvent(event))
        return true
    }

    override fun performEditorAction(editorAction: Int): Boolean {
        if (!isOpen) return false

        if (editorAction == EditorInfo.IME_ACTION_DONE) {
            textController.submit()
        }

        return true
    }

    override fun closeConnection() {
        isOpen = false
    }

    override fun beginBatchEdit(): Boolean {
        println("beginBatchEdit")
        return false
    }

    override fun clearMetaKeyStates(p0: Int): Boolean {
        println("clearMetaKeyStates")
        return false
    }

    override fun commitCompletion(p0: CompletionInfo?): Boolean {
        println("commitCompletion")
        return false
    }

    override fun commitContent(
        p0: InputContentInfo,
        p1: Int,
        p2: Bundle?
    ): Boolean {
        println("commitContent")
        return false
    }

    override fun commitCorrection(p0: CorrectionInfo?): Boolean {
        println("commitCorrection")
        return false
    }

    override fun deleteSurroundingText(p0: Int, p1: Int): Boolean {
        println("deleteSurroundingText")
        return false
    }

    override fun deleteSurroundingTextInCodePoints(p0: Int, p1: Int): Boolean {
        println("deleteSurroundingTextInCodePoints")
        return false
    }

    override fun endBatchEdit(): Boolean {
        println("endBatchEdit")
        return false
    }

    override fun finishComposingText(): Boolean {
        println("finishComposingText")
        return false
    }

    override fun getCursorCapsMode(p0: Int): Int {
        println("getCursorCapsMode")
        return 0
    }

    override fun getExtractedText(
        p0: ExtractedTextRequest,
        p1: Int
    ): ExtractedText? {
        println("getExtractedText")
        return null
    }

    override fun getHandler(): Handler? {
        println("getHandler")
        return null
    }

    override fun getSelectedText(p0: Int): CharSequence {
        println("getSelectedText")
        return ""
    }

    override fun getTextAfterCursor(p0: Int, p1: Int): CharSequence {
        println("getTextAfterCursor")
        return ""
    }

    override fun getTextBeforeCursor(p0: Int, p1: Int): CharSequence {
        println("getTextBeforeCursor")
        return ""
    }

    override fun performContextMenuAction(p0: Int): Boolean {
        println("performContextMenuAction")
        return false
    }

    override fun performPrivateCommand(p0: String?, p1: Bundle?): Boolean {
        println("performPrivateCommand")
        return false
    }

    override fun reportFullscreenMode(p0: Boolean): Boolean {
        println("reportFullscreenMode")
        return false
    }

    override fun requestCursorUpdates(p0: Int): Boolean {
        println("requestCursorUpdates")
        return false
    }

    override fun setComposingRegion(p0: Int, p1: Int): Boolean {
        println("setComposingRegion")
        return false
    }

    override fun setComposingText(p0: CharSequence?, p1: Int): Boolean {
        println("setComposingText")
        return false
    }

    override fun setSelection(p0: Int, p1: Int): Boolean {
        println("setSelection")
        return false
    }

}