package com.rikucherry.simplesnake.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface ScoreDao {
    @Query("SELECT * FROM score_table")
    fun getBestScore(): List<Score>

    @Update
    suspend fun update(score: Score)

    @Query("DELETE FROM score_table")
    suspend fun deleteAll()
}