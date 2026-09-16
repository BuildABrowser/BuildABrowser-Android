package net.buildabrowser.babbrowser.painter.android

import android.graphics.Path
import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec


class ASClipShapeSpec : ClipShapeSpec {

    private val path = Path()
    private var firstItem = true;

    public override fun addPoint(x: Float, y: Float): ClipShapeSpec {
        if (firstItem) {
            this.firstItem = false
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
        return this
    }

    fun path(): Path {
        path.close()
        return path
    }

}