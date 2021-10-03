package com.rikucherry.simplesnake.data

import androidx.annotation.WorkerThread

interface ScoreRepositoryBase {

    @WorkerThread
    suspend fun selectAll(): List<Score>

    @WorkerThread
    suspend fun insert(score: Score)

    @WorkerThread
    suspend fun deleteAll()
}