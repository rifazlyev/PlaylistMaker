package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search)
        val clickSearchButton: View.OnClickListener = object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val searchButtonIntent = Intent(this@MainActivity,SearchActivity::class.java)
                startActivity(searchButtonIntent)
            }
        }
        searchButton.setOnClickListener(clickSearchButton)

        val mediaButton = findViewById<Button>(R.id.media)
        mediaButton.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)

        }

        val settingsButton = findViewById<Button>(R.id.settings)
        settingsButton.setOnClickListener{
            val settingsIntent = Intent(this,SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}