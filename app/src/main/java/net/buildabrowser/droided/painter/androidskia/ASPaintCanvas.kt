package net.buildabrowser.droided.painter.androidskia

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withSave
import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec
import net.buildabrowser.babbrowser.painter.core.FontMetrics
import net.buildabrowser.babbrowser.painter.core.LoadedImage
import net.buildabrowser.babbrowser.painter.core.Paint
import net.buildabrowser.babbrowser.painter.core.PaintBitMap
import net.buildabrowser.babbrowser.painter.core.PaintCanvas
import net.buildabrowser.babbrowser.painter.core.Transform
import java.util.ArrayDeque
import java.util.Deque
import java.util.function.Consumer
import android.graphics.Paint as AndroidPaint

class ASPaintCanvas(private val canvas: Canvas) : PaintCanvas {

    private val matrixStack: Deque<Matrix> = ArrayDeque()
    private val rawPaint = AndroidPaint()
    private val transform = ASTransform(canvas)

    private var currentMatrix = Matrix()
    private var currentPaint = ASPaint()
    private var currentFont = noFont()

    init {
        rawPaint.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        rawPaint.textSize = 12f
        currentPaint.color = 0xFFFFFFFF.toInt()
        currentPaint.font = currentFont
        syncPaint(currentPaint)
    }

    override fun withPaint(
        alterPaintFunc: Consumer<Paint>,
        paintFunc: Consumer<PaintCanvas>
    ) {
        val oldPaint = currentPaint

        // TODO: Re-use paint instances
        val paint = ASPaint()
        paint.color = oldPaint.color
        paint.font = checkNotNull(oldPaint.font)
        alterPaintFunc.accept(paint)
        this.currentPaint = paint
        syncPaint(paint)

        paintFunc.accept(this)

        this.currentPaint = oldPaint
        syncPaint(currentPaint)
    }

    override fun withTransform(
        alterTransformFunc: Consumer<Transform>,
        paintFunc: Consumer<PaintCanvas>
    ) {
        canvas.withSave {
            alterTransformFunc.accept(transform)
            paintFunc.accept(this@ASPaintCanvas)
        }
    }

    override fun saveTransform(paintFunc: Consumer<PaintCanvas>) {
        matrixStack.push(Matrix(currentMatrix))
        paintFunc.accept(this)
        canvas.setMatrix(matrixStack.pop())
    }

    override fun restoreTransform(paintFunc: Consumer<PaintCanvas>) {
        canvas.withMatrix(checkNotNull(matrixStack.peek())) {
            paintFunc.accept(this@ASPaintCanvas)
        }
    }

    override fun withClip(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        paintFunc: Consumer<PaintCanvas>
    ) {
        canvas.withSave {
            canvas.clipRect(RectF(x, y, x + w, y + h))
            paintFunc.accept(this@ASPaintCanvas)
        }
    }

    override fun withShapedClip(
        shapeFunc: Consumer<ClipShapeSpec>,
        paintFunc: Consumer<PaintCanvas>
    ) {
        canvas.withSave {
            val spec = ASClipShapeSpec()
            shapeFunc.accept(spec)
            canvas.clipPath(spec.path())
            paintFunc.accept(this@ASPaintCanvas)
        }
    }

    override fun drawBox(x: Float, y: Float, w: Float, h: Float) {
        // TODO: Coordinate snapping (applies to methods below too)
        canvas.drawRect(RectF(x, y, x + w, y + h), rawPaint)
    }

    override fun drawCircle(x: Float, y: Float, r: Float) {
        canvas.drawCircle(x + r, y + r, r, rawPaint)
    }

    override fun drawText(x: Float, y: Float, text: String) {
        currentFont.drawText(x, y, text, canvas, rawPaint)
    }

    override fun drawImage(
        x: Float,
        y: Float,
        image: LoadedImage
    ) {
        drawImage(x, y, image.width().toFloat(), image.height().toFloat(), image)
    }

    override fun drawImage(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        image: LoadedImage?
    ) {
        require (image is ASLoadedImage) {
            "Passed image must have been loaded via ASResourceLoader!"
        }

        val rect = RectF(x, y, x + w, y + h)
        canvas.drawBitmap(image.bitmap, null, rect, rawPaint)
    }

    override fun drawBitMap(
        x: Int,
        y: Int,
        bitMap: PaintBitMap?
    ) {
        require (bitMap is ASPaintBitMap) {
            "Passed bitmap must have been loaded via ASResourceLoader!"
        }
        bitMap.draw(canvas, null, x, y)
    }

    override fun fontMetrics(): FontMetrics {
        return currentFont.metrics()
    }

    private fun syncPaint(paint: ASPaint) {
        rawPaint.color = paint.color
        rawPaint.style = if (paint.filled) AndroidPaint.Style.FILL else AndroidPaint.Style.STROKE
        rawPaint.strokeWidth = paint.strokeSize
        this.currentFont = paint.font
    }

}
