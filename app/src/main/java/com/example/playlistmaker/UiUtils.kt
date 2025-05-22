package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

object UiUtils {
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun formatTrackTime(durationInMillis: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(durationInMillis)
    }
}
