package com.rikucherry.simplesnake

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_snake_game.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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

        viewModel.applePosition.observe(this, {
            game_view.updateApplePosition(it)
        })

        viewModel.snake.observe(this, {
            game_view.updateSnakeBody(it)
            game_view.invalidate()
        })

        viewModel.realScore.observe(this, {
            score_text.text = it.toString()

            if (it > Constants.secondLevelScore) {
                viewModel.cancelTimer()
                viewModel.startTimerWithPeriod(Constants.secondPeriod, Constants.secondPeriod)
            }

            if (it > Constants.thirdLevelScore) {
                viewModel.cancelTimer()
                viewModel.startTimerWithPeriod(Constants.thirdPeriod, Constants.thirdPeriod)
            }

            if (it > Constants.fourthLevelScore) {
                viewModel.cancelTimer()
                viewModel.startTimerWithPeriod(Constants.fourthPeriod, Constants.fourthPeriod)
            }

        })

        viewModel.lastBest.observe(this, {
            score_best_text.text = it.toString()
        })

        viewModel.state.observe(this, {
            game_view.updateState(it)
            when (it) {
                GameViewModel.State.OVER -> {
                    viewModel.cancelTimer()

                    val lastBestScore = score_best_text.text.toString()
                    val isBest = viewModel.updateScore(lastBestScore.toInt())

                    AlertDialog.Builder(this).setTitle(
                        if (isBest) {
                            "BEST RECORD!"
                        } else {
                            "GAME OVER!"
                        }
                    ).setMessage(
                            "Your score is: ${score_text.text}\n"
                                    + if (isBest) {
                                ""
                            } else {
                                "Current best record is: $lastBestScore"
                            }
                        ).setNegativeButton("Quit") { _, _ ->
                            finish()
                        }.setPositiveButton("Play again") { dialog, _ ->
                            run {
                                dialog.dismiss()
                                viewModel.startGame()
                            }
                        }.setNeutralButton("Share") {
                            dialog, _ ->
                            run {
                                dialog.dismiss()
                                //todo: check permissions
                                shareScreenShot(takeScreenShot(game_frame))
                            }
                        }.setCancelable(
                            false
                        ).show()
                }
            }
        })

        arrow_up_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.UP) }
        arrow_left_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.LEFT) }
        arrow_right_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.RIGHT) }
        arrow_down_button.setOnClickListener { viewModel.changeDirection(GameViewModel.Direction.DOWN) }
        restart_button.setOnClickListener {
            viewModel.cancelTimer()
            viewModel.startGame()
        }

        viewModel.startGame()
    }

    private fun takeScreenShot(view: View): Bitmap {
        val returnBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnBitmap)
        view.draw(canvas)
        return returnBitmap
    }

    private fun shareScreenShot(resource: Bitmap) {
        //todo: do export in coroutine
        var path: String
        try {
            val bytes = ByteArrayOutputStream()
            resource.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val file = File(
                "/storage/emulated/0/DCIM/Camera"
                        + File.separator + "Snake_"
                        + System.currentTimeMillis() / 1000 + ".png"
            )

            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            fo.close()
            path = file.absolutePath
        } catch (e: IOException) {
            path = ""
            e.printStackTrace()
        }


        MediaScannerConnection.scanFile(this, arrayOf(path), null) { _, uri ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/png"

            startActivity(
                Intent.createChooser(shareIntent, "Share")
            )
        }
    }
}