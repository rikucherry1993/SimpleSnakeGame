package com.rikucherry.simplesnake

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_snake_game.*
import java.io.IOException
import java.util.*

private const val WRITE_EXTERNAL_STORAGE_REQUEST = 0X1000

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

                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                                    && !isWritePermissionGranted()
                                ) {
                                    requestWritePermission()
                                } else {
                                    shareScreenShot(takeScreenShot(game_frame.rootView))
                                }
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

    private fun shareScreenShot(bmp: Bitmap) {
        var path = ""
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Snake_" + UUID.randomUUID().toString())
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }

        try {
            contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                path = convertUriToPath(uri)
                contentResolver.openOutputStream(uri).use { outputStream ->
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }
        } catch (e: IOException) {
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

    private fun convertUriToPath(uri: Uri) : String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver.query(uri, proj, null, null, null)
            val columnIdx = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) ?: -1
            cursor?.moveToFirst()
            return cursor?.getString(columnIdx) ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return ""
    }

    private fun isWritePermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PERMISSION_GRANTED

    private fun requestWritePermission() {
        if (!isWritePermissionGranted()) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, WRITE_EXTERNAL_STORAGE_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            WRITE_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isEmpty() || grantResults[0] != PERMISSION_GRANTED) {
                    Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName")).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }.also { intent ->
                        startActivity(intent)
                    }
                }
                return
            }
        }

    }

}