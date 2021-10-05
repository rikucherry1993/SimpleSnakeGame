package com.rikucherry.simplesnake.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ScoreDaoTest {

    private lateinit var database: ScoreDatabase
    private lateinit var dao: ScoreDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ScoreDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.scoreDao()
    }

    @Test
    fun insert_and_select_score() = runBlocking {
        dao.insert(Score(1,100))
        Truth.assertThat(dao.getBestScore()[0].bestScore).isEqualTo(100)
    }

    @Test
    fun insert_and_delete_score() = runBlocking {
        dao.insert(Score(1,100))
        dao.deleteAll()
        Truth.assertThat(dao.getBestScore().size).isEqualTo(0)
    }

    @After
    fun tearDown() {
        database.clearAllTables()
        database.close()
    }
}