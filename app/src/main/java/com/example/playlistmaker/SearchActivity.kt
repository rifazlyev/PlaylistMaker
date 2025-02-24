package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapter.TrackAdapter
import com.example.playlistmaker.model.Track

class SearchActivity : AppCompatActivity() {
    private var textValue: String = ""

    companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }

    private lateinit var backSearch: LinearLayout
    private lateinit var buttonBack: ImageButton
    private lateinit var inputEditText: EditText
    private lateinit var buttonClear: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backSearch = findViewById(R.id.search_screen)
        buttonBack = findViewById(R.id.back_button_search_screen)
        inputEditText = findViewById(R.id.searchEditText)
        buttonClear = findViewById(R.id.clearButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backSearch.setOnClickListener {
            finish()
        }

        buttonBack.setOnClickListener {
            backSearch.performClick()
        }

        buttonClear.setOnClickListener {
            inputEditText.setText("")
            inputEditText.clearFocus()
            hideKeyboard(inputEditText)
            buttonClear.visibility = View.GONE
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrBlank()) {
                    textValue = p0.toString()
                    buttonClear.visibility = clearButtonVisibility(p0)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }

        inputEditText.addTextChangedListener(textWatcher)

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        val listOfTracks = listOf(
            Track(
                trackName = "Smells Like Teen Spirit",
                artistName = "Nirvana",
                trackTime = "5:01",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                trackName = "Billie Jean",
                artistName = "Michael Jackson",
                trackTime = "4:35",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                trackName = "Stayin' Alive",
                artistName = "Bee Gees",
                trackTime = "4:10",
                artworkUrl100 = "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                trackName = "Whole Lotta Love",
                artistName = "Led Zeppelin",
                trackTime = "5:33",
                artworkUrl100 = "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                trackName = "Sweet Child O'Mine",
                artistName = "Guns N' Roses",
                trackTime = "5:03",
                artworkUrl100 = "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TrackAdapter(track = listOfTracks)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyboard(view: View) {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(SEARCH_TEXT_KEY, textValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        inputEditText.setText(restoredText)
    }
}
