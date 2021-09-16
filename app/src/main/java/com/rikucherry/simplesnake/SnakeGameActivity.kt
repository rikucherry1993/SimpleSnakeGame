package com.rikucherry.simplesnake

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_snake_game.*

class SnakeGameActivity : AppCompatActivity() {
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake_game)
        setSupportActionBar(game_toolbar)
        game_toolbar.setNavigationIcon(R.drawable.ic_toolbar_home)
        game_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        //observe position of apple
        viewModel.applePosition.observe(this, {
            game_view.updateApplePosition(it)
        })

        viewModel.snake.observe(this, {
            game_view.updateSnakeBody(it)
        })

        viewModel.startGame()
    }
}