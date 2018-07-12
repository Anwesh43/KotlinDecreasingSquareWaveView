package com.anwesh.uiprojects.decreasingsquarewaveview

/**
 * Created by anweshmishra on 12/07/18.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.content.Context
import android.graphics.Color

val nodes : Int = 5
class DecreasingSquareWaveView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class DSWState(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class DSWAnimator(var view : View, var animated : Boolean = false) {

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }
    }

    data class DSWNode(var i : Int, val state : DSWState = DSWState()) {

        private var next : DSWNode? = null

        private var prev : DSWNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = DSWNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = w / nodes
            paint.strokeWidth = Math.min(w, h) / 60
            val getScale : (Int) -> Float = {i -> Math.min(i * 1f / 3, Math.max(0f, state.scale - (i * 1f) / 3)) * 3f}
            canvas.save()
            canvas.translate(i * gap, h/2)
            canvas.drawLine(0f, -gap * getScale(0), 0f, -gap, paint)
            canvas.drawLine(gap * getScale(1), -gap, gap, -gap, paint)
            canvas.drawLine(gap, -gap + gap * getScale(2),gap, 0f, paint)
            canvas.restore()
            next?.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            state.update {
                stopcb(i, it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : DSWNode {
            var curr : DSWNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class DecreasingSquareWave(var i : Int) {

        private var curr : DSWNode = DSWNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            paint.color = Color.parseColor("#2E7D32")
            paint.strokeCap = Paint.Cap.ROUND
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            curr.update {i, scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(i, scale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : DecreasingSquareWaveView) {

        private var dsw : DecreasingSquareWave = DecreasingSquareWave(0)

        private var animator : DSWAnimator = DSWAnimator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            dsw.draw(canvas, paint)
            animator.animate {
                dsw.update {j, scale ->
                    animator.stop()
                    when (scale) {
                        1f -> {}
                        0f -> {}
                    }
                }
            }
        }

        fun handleTap() {
            dsw.startUpdating {
                animator.start()
            }
        }
    }
}