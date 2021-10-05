package com.rikucherry.simplesnake


import com.rikucherry.simplesnake.data.MockScoreRepository
import com.rikucherry.simplesnake.data.Score
import org.junit.Before


class GameViewModelTest {

    private lateinit var viewModel: GameViewModel
    private lateinit var mockData: MutableList<Score>

    @Before
    fun setUp() {
        mockData = mutableListOf(Score(1,25))
        // the instance of viewModel class that we want to test on
        viewModel = GameViewModel(MockScoreRepository(mockData))
    }

}