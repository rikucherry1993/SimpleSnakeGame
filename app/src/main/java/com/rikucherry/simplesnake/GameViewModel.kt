package com.rikucherry.simplesnake

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rikucherry.simplesnake.data.Score
import com.rikucherry.simplesnake.data.ScoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

class GameViewModel(private val repository: ScoreRepository) : ViewModel() {

    private var snakeBody = mutableListOf<Position>()
    private val initialHead = Position(Constants.initialHeadX, Constants.initialHeadY)
    private var direction: Direction = Direction.LEFT
    private var score = 0
    private lateinit var timer: Timer
    var applePosition = MutableLiveData<Position>()
    var snake = MutableLiveData<List<Position>>()
    var state = MutableLiveData<State>()
    var realScore = MutableLiveData<Int>()
    var lastBest = MutableLiveData<Int>()

    data class Position(var x: Int, var y: Int)
    enum class State { STARTED, OVER }
    enum class Direction { LEFT, UP, RIGHT, DOWN }

    fun startGame() {
        //Initial states
        state.value = State.STARTED
        score = 0
        realScore.value = score
        direction = Direction.LEFT
        setLastBest()

        //Generate snake body
        snakeBody.clear()
        val head = initialHead
        snakeBody.add(head)
        for (i in 1 until Constants.initialSnakeSize) {
            snakeBody.add(Position(head.x + i, head.y))
        }
        snake.value = snakeBody

        //Generate new position for apple randomly
        applePosition.value = generatePosition(snakeBody)

        //start timer
        startTimerWithPeriod(Constants.firstPeriod, Constants.firstPeriod)
    }

    /**
     * Generate new position for apple randomly
     * @param exclude
     */
    private fun generatePosition(exclude: List<Position>?): Position {
        var position: Position? = null
        if (exclude != null) {
            while (position == null || exclude.contains(position)) {
                position =
                    Position(Random.nextInt(Constants.range), Random.nextInt(Constants.range))
            }
        } else {
            position = Position(Random.nextInt(Constants.range), Random.nextInt(Constants.range))
        }
        return position
    }

    fun startTimerWithPeriod(initialDelay: Long, period: Long) {
        timer = fixedRateTimer("periodically timer", false, initialDelay, period) {
            // without copy(), snake body list would be changed directly
            val head = snakeBody.first().copy().apply {
                when (direction) {
                    Direction.LEFT -> x--
                    Direction.UP -> y--
                    Direction.RIGHT -> x++
                    Direction.DOWN -> y++
                }
                //after changing new head's position, check if a)body contains head b) head get out of canvas
                if (snakeBody.contains(this) || x < 0 || x > Constants.range - 1 || y < 0 || y > Constants.range - 1) {
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


    fun cancelTimer() {
        timer.cancel()
    }

    fun changeDirection(dir: Direction) {
        if (!isReverse(direction, dir)) {
            direction = dir
        }
    }

    fun setLastBest() {
        viewModelScope.launch {
            lastBest.postValue(withContext(Dispatchers.IO) {
                if (repository.selectAll().isEmpty()) 0 else repository.selectAll()[0].bestScore
            })
        }
    }

    fun updateScore(bestScore: Int): Boolean {
        return (score > bestScore).also {
            if (it) {
                viewModelScope.launch {
                    repository.deleteAll()
                    repository.insert(Score(1, score))
                }
            }
        }
    }

    private fun isReverse(old: Direction, new: Direction): Boolean {
        return (old == Direction.UP && new == Direction.DOWN || old == Direction.DOWN && new == Direction.UP) ||
                (old == Direction.LEFT && new == Direction.RIGHT || old == Direction.RIGHT && new == Direction.LEFT)
    }
}

/**
 * View model Factory
 * reference:https://developer.android.com/codelabs/android-room-with-a-view-kotlin#9
 */
class GameViewModelFactory(private val repository: ScoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}