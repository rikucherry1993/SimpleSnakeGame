package com.rikucherry.simplesnake.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.rikucherry.simplesnake.GameViewModel.Position
import com.rikucherry.simplesnake.R

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paintApple = Paint().apply {
        color = context.resources.getColor(R.color.apple_color)
    }

    private val paintSnake = Paint().apply {
        color = context.resources.getColor(R.color.snake_color_alive)
    }

    private var applePosition: Position? = null
    private var snake : List<Position>? = null
    private var sideLength = 0f
    private var offset = 1f

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            applePosition?.apply {
                canvas.drawRect(
                    (x * sideLength) + offset,
                    (y * sideLength) + offset,
                    ((x + 1) * sideLength) - offset,
                    ((y + 1) * sideLength) - offset,
                    paintApple
                )
            }
            snake?.forEach {
                canvas.drawRect(
                    (it.x * sideLength) + offset,
                    (it.y * sideLength) + offset,
                    ((it.x + 1) * sideLength) - offset,
                    ((it.y + 1) * sideLength) - offset,
                    paintSnake
                )
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        sideLength = width.toFloat() / 20
    }


    fun updateApplePosition(position: Position) {
        applePosition = position
    }

    fun updateSnakeBody(body: List<Position>) {
        snake = body
    }
}