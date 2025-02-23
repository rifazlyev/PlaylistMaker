package com.example.playlistmaker.viewHolder

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackName = itemView.findViewById<TextView>(R.id.track_title)
    private val artistName = itemView.findViewById<TextView>(R.id.artist_title)
    private  val trackTime = itemView.findViewById<TextView>(R.id.track_duration)
    private  val trackImage = itemView.findViewById<ImageView>(R.id.track_image)

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun bind(track: Track){
        trackName.text = track.trackName
        artistName.text= track.artistName
        trackTime.text = track.trackTime

        val radiusPx = dpToPx(2F, itemView.context)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.track_placeholder)
            .centerInside()
            .transform(RoundedCorners(radiusPx))
            .into(trackImage)
    }
}
