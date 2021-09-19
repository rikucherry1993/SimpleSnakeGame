package com.rikucherry.simplesnake

import android.app.Application
import com.rikucherry.simplesnake.data.ScoreDataBase
import com.rikucherry.simplesnake.data.ScoreRepository

class SnakeGameApplication : Application() {

    val dataBase by lazy { ScoreDataBase.getDatabase(this)}
    val repository by lazy { ScoreRepository(dataBase.scoreDao())}
}