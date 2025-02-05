package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Тут сделал кликабельным весь контейнер
        val backSettings = findViewById<LinearLayout>(R.id.settings_screen)
        backSettings.setOnClickListener {
            val buttonBackIntent = Intent(this, MainActivity::class.java)
            startActivity(buttonBackIntent)
        }

        //Тут кликабельна и сама 'стрелочка'
        val buttonBack= findViewById<ImageButton>(R.id.back_button_settings_screen)
        buttonBack.setOnClickListener {
            val buttonBackIntent = Intent(this, MainActivity::class.java)
            startActivity(buttonBackIntent)
        }

        val buttonShareApp = findViewById<LinearLayout>(R.id.share_app)
        buttonShareApp.setOnClickListener{
            val message = "https://practicum.yandex.ru/android-developer"
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Choose the app"))
        }
    }
}
