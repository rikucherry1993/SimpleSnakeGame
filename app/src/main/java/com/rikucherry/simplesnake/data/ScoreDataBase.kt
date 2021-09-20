package com.rikucherry.simplesnake.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Score::class], version = 1, exportSchema = true)
abstract class ScoreDataBase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        private const val DB_NAME = "score_database"

        @Volatile
        private var INSTANCE: ScoreDataBase? = null

        fun getDatabase(context: Context): ScoreDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScoreDataBase::class.java,
                    DB_NAME,
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }

}