package com.rikucherry.simplesnake

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_snake_game.*

class SnakeGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake_game)
        setSupportActionBar(game_toolbar)
        game_toolbar.setNavigationIcon(R.drawable.ic_toolbar_home)
        game_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}