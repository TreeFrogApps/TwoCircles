package com.treefrogapps.circles.twocircles

import android.content.Context
import android.graphics.*
import android.graphics.Path.Direction.CW
import android.util.*
import android.view.*
import android.view.MotionEvent.*

open class CircleView : View, View.OnTouchListener {

    private val paint = Paint()
    private val density = context.resources.displayMetrics.density
    private var circleA = Path()
    private var circleB = Path()

    private var radius: Float = 20.0F * density
    private var drawCircle: Boolean = false

    constructor(context: Context) : super(context) {
        setParams()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setParams()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setParams()
    }

    fun setCircleWidth(width: Float) {
        radius = width * density
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setOnTouchListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        setOnTouchListener(null)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        return when (event?.actionMasked) {
            ACTION_MOVE,
            ACTION_DOWN -> drawCircle(event.x, event.y)
            ACTION_UP   -> clearCircle()
            else        -> false
        }
    }

    private fun clearCircle(): Boolean {
        circleB.reset()
        drawCircle = false
        invalidate()
        return true
    }

    private fun drawCircle(x: Float, y : Float) : Boolean {
        circleB.reset()
        drawCircle = true

        val centerX = ((width / 2.0F))
        val centerY = ((height / 2.0F))

        // find actual distance from center from outer circle edge - hypotenuse
        val xPos = Math.abs((centerX - x).toDouble())
        val yPos = Math.abs((centerY - y).toDouble())
        val actualDist = Math.sqrt(Math.pow(xPos, 2.0) + Math.pow(yPos, 2.0)) - radius

        if(actualDist <= radius){
            // work out closest position possible at current angle (work out adjacent (x) & opposite (y))
            val angle = Math.atan(yPos / xPos)
            var xNew = Math.cos(angle) * (radius * 2)
            var yNew = Math.sin(angle) * (radius * 2)
            // add/minus distance from center based on quadrant
            if(x > centerX) xNew += centerX else xNew = centerX - xNew
            if(y > centerY) yNew += centerY else yNew = centerY - yNew
            circleB.addCircle(xNew.toFloat(), yNew.toFloat(), this.radius, CW)
        } else {
            circleB.addCircle(x, y, this.radius, CW)
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        Log.e("MARK", "top $top, bottom $bottom, left $left, right $right")
        circleA.reset()
        circleA.addCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, CW)

        canvas?.apply {
            paint.color = Color.RED
            drawPath(circleA, paint)
            if (drawCircle) {
                paint.color = Color.BLUE
                drawPath(circleB, paint)
            }
        }
    }

    private fun setParams() {
        setWillNotDraw(false)
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = false
    }
}