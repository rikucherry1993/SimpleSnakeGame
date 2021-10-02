package com.rikucherry.simplesnake

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rikucherry.simplesnake.data.ScoreDao
import com.rikucherry.simplesnake.data.ScoreDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SnakeGameActivityTest {

    private lateinit  var dao: ScoreDao
    private lateinit  var database: ScoreDatabase

    //todo: use alternative
    @Rule
    val SUT: IntentsTestRule<SnakeGameActivity> = IntentsTestRule(SnakeGameActivity::class.java, true, false)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<SnakeGameActivity>()
        database = Room.databaseBuilder(context, ScoreDatabase::class.java, "score_database").build()
        dao = database.scoreDao()
    }



    @After
    fun closeDb() {
        try {
            database.clearAllTables()
            database.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}