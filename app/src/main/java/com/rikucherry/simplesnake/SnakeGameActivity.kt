package com.rikucherry.simplesnake

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_snake_game.*

class SnakeGameActivity : AppCompatActivity() {
    private val viewModel: GameViewModel by viewModels {
        GameViewModelFactory((application as SnakeGameApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake_game)
        setSupportActionBar(game_toolbar)
        game_toolbar.setNavigationIcon(R.drawable.ic_toolbar_home)
        game_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        //observe position of apple
        viewModel.applePosition.observe(this, {
            game_view.updateApplePosition(it)
        })

        viewModel.snake.observe(this, {
            game_view.updateSnakeBody(it)
            game_view.invalidate()
        })

        viewModel.state.observe(this, {
            game_view.updateState(it)
            when (it) {
                GameViewModel.State.OVER -> {
                    viewModel.stopTimer()

                    val lastBestScore = viewModel.lastBestScore
                    val isBest = viewModel.updateScore(lastBestScore)

                    AlertDialog.Builder(this).setTitle(
                        if (isBest) {"BEST RECORD!"} else {"GAME OVER!"})
                        .setMessage("Your score is: ${score_text.text}. "
                            + if (isBest) {""} else {"Current best record is: $lastBestScore"})
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

        arrow_up_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.UP) }
        arrow_left_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.LEFT) }
        arrow_right_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.RIGHT) }
        arrow_down_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.DOWN) }
        restart_button.setOnClickListener {
            viewModel.stopTimer()
            viewModel.startGame()
        }

        viewModel.startGame()
    }
}