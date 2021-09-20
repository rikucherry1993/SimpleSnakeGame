package com.rikucherry.simplesnake.data

import androidx.annotation.WorkerThread

class ScoreRepository(private val scoreDao: ScoreDao) {

    fun selectAll() = scoreDao.getBestScore()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(score: Score) {
        scoreDao.insert(score)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        scoreDao.deleteAll()
    }

}