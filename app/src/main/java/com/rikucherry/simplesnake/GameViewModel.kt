package com.rikucherry.simplesnake

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel : ViewModel() {
    private val range = 20
    private var snakeSize = 4
    private var snakeBody= mutableListOf<Position>()
    private val initialHead = Position(10,10)
    var applePosition = MutableLiveData<Position>()
    var snake = MutableLiveData<List<Position>>()

    data class Position(var x: Int, var y: Int)
    enum class State { STARTED, PAUSED, OVER }
    enum class Direction { LEFT, UP, RIGHT, DOWN }

    fun startGame() {
        //Generate snake body
        snakeBody.clear()
        val head = initialHead
        snakeBody.add(head)
        for (i in 1 until snakeSize) {
            snakeBody.add(Position(head.x + i, head.y))
        }
        snake.value = snakeBody

        //Generate new position for apple randomly
        applePosition.value = generatePosition(snakeBody)
    }

    /**
     * Generate new position for apple randomly
     * @param exclude
     */
    private fun generatePosition(exclude: List<Position>?): Position {
        var position: Position? = null
        if (exclude != null) {
            while (position == null || exclude.contains(position)) {
                position = Position(Random.nextInt(range), Random.nextInt(range))
            }
        } else {
            position = Position(Random.nextInt(range), Random.nextInt(range))
        }
        return position
    }
}