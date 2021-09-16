package com.rikucherry.simplesnake

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
            game_view.invalidate()
        })

        viewModel.state.observe(this, {
            when(it) {
                //todo: change to alert dialog
                GameViewModel.State.OVER -> {
                    viewModel.stopTimer()
                    AlertDialog.Builder(this).setTitle("GAME OVER!!")
                        .setMessage("Your score is: ${score_text.text}")
                        .setNegativeButton("Quit") { _, _ ->
                            finish()
                        }.setPositiveButton("Play again") { dialog, _ ->
                            run {
                                dialog.dismiss()
                                viewModel.startGame()
                            }
                        }.setCancelable(
                            false
                        ).show()
                }
            }
        })

        viewModel.realScore.observe(this, {
            score_text.text = it.toString()
        })

        arrow_up_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.UP)}
        arrow_left_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.LEFT)}
        arrow_right_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.RIGHT)}
        arrow_down_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.DOWN)}
        restart_button.setOnClickListener {
            viewModel.stopTimer()
            viewModel.startGame()
        }

        viewModel.startGame()
    }
}