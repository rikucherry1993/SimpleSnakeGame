package com.rikucherry.simplesnake.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.rikucherry.simplesnake.GameViewModel.Position
import com.rikucherry.simplesnake.GameViewModel.State
import com.rikucherry.simplesnake.R

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paintApple = Paint().apply {
        color = context.resources.getColor(R.color.apple_color)
    }

    private val paintSnake = Paint().apply {
        color = context.resources.getColor(R.color.snake_color_alive)
    }

    private var applePosition = Position(19, 19)
    private var snake : List<Position>? = null
    private var state : State? = null
    private var dim = 0
    private var offset = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            applePosition?.apply {
                canvas.drawRect(
                    (x * dim + offset).toFloat(),
                    (y * dim + offset).toFloat(),
                    ((x + 1) * dim + offset).toFloat(),
                    ((y + 1) * dim + offset).toFloat(),
                    paintApple
                )
            }
            snake?.onEach {
//                canvas.drawRect()
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        dim = width / 20
    }

    /**
     * Generate new position for apple randomly
     */
    fun generateNew(position: Position) {
        if (applePosition == null || applePosition == snake?.get(0)) {
            //todo: generate new
        }
    }
}