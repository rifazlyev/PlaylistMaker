package com.example.playlistmaker.common

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE

object UiUtils {
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun Activity.hideKeyboard(view: View) {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
