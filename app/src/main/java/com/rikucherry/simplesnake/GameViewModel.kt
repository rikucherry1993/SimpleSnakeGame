package com.rikucherry.simplesnake

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    data class Position(var x: Int, var y: Int)

    var applePosition = MutableLiveData<Position>()



    enum class State { STARTED, PAUSED, OVER }
    enum class Direction { LEFT, UP, RIGHT, DOWN }
}