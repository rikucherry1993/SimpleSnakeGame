package com.rikucherry.simplesnake.data

import androidx.annotation.WorkerThread

class ScoreRepository(private val scoreDao: ScoreDao) : ScoreRepositoryBase {

    @WorkerThread
    override suspend fun selectAll() = scoreDao.getBestScore()

    @WorkerThread
    override suspend fun insert(score: Score) {
        scoreDao.insert(score)
    }

    @WorkerThread
    override suspend fun deleteAll() {
        scoreDao.deleteAll()
    }

}