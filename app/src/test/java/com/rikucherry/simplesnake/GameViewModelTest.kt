package com.rikucherry.simplesnake


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.rikucherry.simplesnake.data.MockScoreRepository
import com.rikucherry.simplesnake.data.Score
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*
import kotlin.concurrent.schedule

@RunWith(JUnit4::class)
class GameViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @kotlinx.coroutines.ObsoleteCoroutinesApi
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var viewModel: GameViewModel
    private lateinit var mockData: MutableList<Score>

    //Test data
    companion object {
        private const val BEST_SCORE = 25
        private const val PERIOD: Long = 250
    }


    @Before
    @kotlinx.coroutines.ObsoleteCoroutinesApi
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        mockData = mutableListOf(Score(1, BEST_SCORE))
        // the instance of viewModel class that we want to test on
        viewModel = GameViewModel(MockScoreRepository(mockData))
    }

    @Test
    fun start_game_with_state_started() {
        viewModel.startGame()
        viewModel.cancelTimer()

        val state = viewModel.state.getOrAwaitValueTest()
        assertThat(state).isEqualTo(GameViewModel.State.STARTED)
    }

    @Test
    fun start_game_with_score_zero() {
        viewModel.startGame()
        viewModel.cancelTimer()

        val score = viewModel.realScore.getOrAwaitValueTest()
        assertThat(score).isEqualTo(0)
    }

    @Test
    fun start_game_with_last_best() {
        viewModel.startGame()
        viewModel.cancelTimer()

        val lastBest = viewModel.lastBest.getOrAwaitValueTest()
        assertThat(lastBest).isEqualTo(BEST_SCORE)
    }

    @Test
    fun start_game_with_original_snake_length() {
        viewModel.startGame()
        viewModel.cancelTimer()

        val snakeLength = viewModel.snake.getOrAwaitValueTest().size
        assertThat(snakeLength).isEqualTo(Constants.initialSnakeSize)
    }

    @Test
    fun apple_position_is_not_overlapped_by_snake_body() {
        viewModel.startGame()
        viewModel.cancelTimer()

        val snakeBody = viewModel.snake.getOrAwaitValueTest()
        val applePosition = viewModel.applePosition.getOrAwaitValueTest()
        assertThat(snakeBody).doesNotContain(applePosition)
    }

    @Test
    fun game_over_after_over_ten_times_of_period_spent() {
        viewModel.startGame()
        Timer().schedule(11 * Constants.firstPeriod) {
            viewModel.cancelTimer()
            val state = viewModel.state.getOrAwaitValueTest()
            assertThat(state).isEqualTo(GameViewModel.State.OVER)
        }
    }

   @Test
   fun change_direction_up_succeed() {
       val initialHead = GameViewModel.Position(Constants.initialHeadX, Constants.initialHeadY)
       viewModel.startGame()
       val applePosition = viewModel.applePosition.getOrAwaitValueTest()
       viewModel.changeDirection(GameViewModel.Direction.UP)

       Timer().schedule(2 * Constants.firstPeriod) {
           viewModel.cancelTimer()
           val newHead = viewModel.snake.getOrAwaitValueTest()[0]
           assertThat(newHead.y).isLessThan(initialHead.y)
       }
   }


    @Test
    fun change_direction_down_succeed() {
        val initialHead = GameViewModel.Position(Constants.initialHeadX, Constants.initialHeadY)
        viewModel.startGame()
        val applePosition = viewModel.applePosition.getOrAwaitValueTest()
        viewModel.changeDirection(GameViewModel.Direction.DOWN)

        Timer().schedule(2 * Constants.firstPeriod) {
            viewModel.cancelTimer()
            val newHead = viewModel.snake.getOrAwaitValueTest()[0]
            assertThat(newHead.y).isGreaterThan(initialHead.y)
        }
    }


    @After
    @kotlinx.coroutines.ObsoleteCoroutinesApi
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

}