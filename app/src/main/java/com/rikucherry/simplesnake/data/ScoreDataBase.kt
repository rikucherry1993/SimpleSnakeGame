package com.rikucherry.simplesnake.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Score::class], version = 1, exportSchema = true)
abstract class ScoreDataBase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    private class ScoreDatabaseCallback (private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                database -> scope.launch {
                    //do nothing
            }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ScoreDataBase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ScoreDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScoreDataBase::class.java,
                    "score_database",
                ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries() //todo: Due to "Cannot access database on the main thread since it may potentially lock the UI for a long period of time" bad! resolve later
                    .addCallback(ScoreDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

}