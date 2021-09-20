package com.rikucherry.simplesnake.data

import androidx.annotation.WorkerThread

class ScoreRepository(private val scoreDao: ScoreDao) {

    @WorkerThread
    suspend fun selectAll() = scoreDao.getBestScore()

    @WorkerThread
    suspend fun insert(score: Score) {
        scoreDao.insert(score)
    }

    @WorkerThread
    suspend fun deleteAll() {
        scoreDao.deleteAll()
    }

}