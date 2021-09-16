package com.rikucherry.simplesnake

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

class GameViewModel : ViewModel() {
    private val range = 20
    private var snakeSize = 4
    private var snakeBody= mutableListOf<Position>()
    private val initialHead = Position(10,10)
    private var direction : Direction = Direction.LEFT
    private var score = 0
    private lateinit var timer: Timer
    var applePosition = MutableLiveData<Position>()
    var snake = MutableLiveData<List<Position>>()
    var state = MutableLiveData<State>()
    var realScore = MutableLiveData<Int>()

    data class Position(var x: Int, var y: Int)
    enum class State { STARTED, PAUSED, OVER }
    enum class Direction { LEFT, UP, RIGHT, DOWN }

    fun startGame() {
        //Initial states
        state.value = State.STARTED
        score = 0
        realScore.value = score
        direction = Direction.LEFT

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

        //start timer
        timer = fixedRateTimer("periodically timer", false, 200, 200) {
            // without copy(), snake body list would be changed directly
            val head = snakeBody.first().copy().apply {
                when(direction) {
                    Direction.LEFT -> x--
                    Direction.UP -> y--
                    Direction.RIGHT -> x++
                    Direction.DOWN -> y++
                }
                //after changing new head's position, check if a)body contains head b) head get out of canvas
                if (snakeBody.contains(this) || x < 0 || x > range - 1 || y < 0 || y > range - 1 ) {
                    state.postValue(State.OVER)
                }
            }
            snakeBody.add(0, head)
            if (head == applePosition.value) {
                // got the apple! generate the next apple
                applePosition.postValue(generatePosition(snakeBody))
                score++
                realScore.postValue(score)
            } else {
                snakeBody.removeLast()
            }
            snake.postValue(snakeBody)
        }
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

    fun stopTimer() {
        timer.cancel()
    }

    fun changeDirection(dir:Direction) {
        direction = dir
    }
}