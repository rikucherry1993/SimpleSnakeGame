package com.rikucherry.simplesnake

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel : ViewModel() {

    data class Position(var x: Int, var y: Int)
    val range = 20
    var applePosition = MutableLiveData<Position>()



    enum class State { STARTED, PAUSED, OVER }
    enum class Direction { LEFT, UP, RIGHT, DOWN }

    fun startGame() {
        //Generate new position for apple randomly
        applePosition.value = Position(Random.nextInt(range), Random.nextInt(range))
    }
}