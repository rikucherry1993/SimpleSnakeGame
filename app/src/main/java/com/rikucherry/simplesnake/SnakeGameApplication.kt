package com.rikucherry.simplesnake

import android.app.Application
import com.rikucherry.simplesnake.data.ScoreDataBase
import com.rikucherry.simplesnake.data.ScoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SnakeGameApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val dataBase by lazy { ScoreDataBase.getDatabase(this, applicationScope)}
    val repository by lazy { ScoreRepository(dataBase.scoreDao())}
}