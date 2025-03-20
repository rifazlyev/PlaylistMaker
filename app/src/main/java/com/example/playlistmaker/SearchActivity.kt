package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapter.TrackAdapter
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.requests.ITunesApiService
import com.example.playlistmaker.responses.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var textValue: String = ""
    private val baseUrl = "https://itunes.apple.com"

    companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }

    private lateinit var backSearch: LinearLayout
    private lateinit var buttonBack: ImageButton
    private lateinit var inputEditText: EditText
    private lateinit var buttonClear: ImageView
    private lateinit var imagePlaceholder: ImageView
    private lateinit var textPlaceholder: TextView
    private lateinit var refreshButton: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApiService::class.java)
    private val listOfTrack: ArrayList<Track> = ArrayList()
    private val adapter = TrackAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backSearch = findViewById(R.id.search_screen)
        buttonBack = findViewById(R.id.back_button_search_screen)
        inputEditText = findViewById(R.id.searchEditText)
        buttonClear = findViewById(R.id.clearButton)
        imagePlaceholder = findViewById(R.id.image_placeholder)
        textPlaceholder = findViewById(R.id.text_placeholder)
        refreshButton = findViewById(R.id.refresh_button)

        adapter.trackList = listOfTrack

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = inputEditText.text.toString()
                if (query.isNotEmpty()) {
                    search(query)
                }
                true
            } else {
                false
            }
        }

        backSearch.setOnClickListener {
            finish()
        }

        buttonBack.setOnClickListener {
            backSearch.performClick()
        }

        buttonClear.setOnClickListener {
            inputEditText.setText("")
            listOfTrack.clear()
            inputEditText.clearFocus()
            hideKeyboard(inputEditText)
            buttonClear.visibility = View.GONE
            allViewIsGone()
        }

        refreshButton.setOnClickListener {
            search(inputEditText.text.toString())
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

    private fun showError(message: String, iconResId: Int, showRefreshButton: Boolean = false) {
        listOfTrack.clear()
        adapter.notifyDataSetChanged()

        imagePlaceholder.apply {
            setBackgroundResource(iconResId)
            visibility = View.VISIBLE
        }

        textPlaceholder.apply {
            text = message
            visibility = View.VISIBLE
        }

        refreshButton.visibility = if (showRefreshButton) View.VISIBLE else View.GONE
    }

    private fun allViewIsGone() {
        imagePlaceholder.visibility = View.GONE
        textPlaceholder.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun search(query: String) {
        if (query.isBlank()) {
            allViewIsGone()
            return
        }

        iTunesService.search(query).enqueue(
            object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    listOfTrack.clear()
                    adapter.notifyDataSetChanged()
                    if (response.code() == 200) {
                        allViewIsGone()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            listOfTrack.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        } else {
                            showError(getString(R.string.empty_list), R.drawable.ic_empty_list)
                        }
                    } else {
                        showError(
                            getString(R.string.something_wrong),
                            R.drawable.ic_something_wrong,
                            true
                        )
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    showError(
                        getString(R.string.something_wrong),
                        R.drawable.ic_something_wrong,
                        true
                    )
                }
            }
        )
    }
}
