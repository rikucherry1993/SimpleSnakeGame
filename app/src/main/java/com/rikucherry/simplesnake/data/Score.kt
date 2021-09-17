package com.rikucherry.simplesnake.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "score_table")
data class Score(
    @PrimaryKey val _id: Int,
    @ColumnInfo(name = "best_score") val bastScore: Int?,
    @ColumnInfo(name = "record_date") val recordDate: Date?
)