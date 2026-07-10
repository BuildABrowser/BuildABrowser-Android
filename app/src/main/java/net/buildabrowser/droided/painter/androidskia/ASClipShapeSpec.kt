package net.buildabrowser.droided.painter.androidskia

import android.graphics.Path
import android.graphics.Point
import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec
import java.util.function.IntFunction
import kotlin.collections.ArrayList
import kotlin.collections.MutableList


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