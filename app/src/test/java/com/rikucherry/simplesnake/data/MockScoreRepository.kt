package com.rikucherry.simplesnake.data

import androidx.annotation.WorkerThread

/**
 * Mock of ScoreRepository for viewModel test
 */
class MockScoreRepository(private val mockData: MutableList<Score>) : ScoreRepositoryBase {

    @WorkerThread
    override suspend fun selectAll(): List<Score> {
        return mockData
    }

    @WorkerThread
    override suspend fun insert(score: Score) {
        mockData.add(score)
    }

    @WorkerThread
    override suspend fun deleteAll() {
        mockData.clear()
    }
}