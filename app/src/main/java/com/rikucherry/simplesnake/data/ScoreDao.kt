package com.rikucherry.simplesnake.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Query("SELECT * FROM score_table")
    fun getBestScore(): List<Score>

    @Insert
    suspend fun insert(score: Score)

    @Query("DELETE FROM score_table")
    suspend fun deleteAll()
}