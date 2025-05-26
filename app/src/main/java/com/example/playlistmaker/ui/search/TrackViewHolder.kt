package com.example.playlistmaker.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.UiUtils.dpToPx
import com.example.playlistmaker.UiUtils.formatTrackTime
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.model.TrackUi

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName = itemView.findViewById<TextView>(R.id.track_title)
    private val artistName = itemView.findViewById<TextView>(R.id.artist_title)
    private val trackTime = itemView.findViewById<TextView>(R.id.track_duration)
    private val trackImage = itemView.findViewById<ImageView>(R.id.track_image)

    fun bind(track: TrackUi) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.formattedTime

        val radiusPx = dpToPx(2F, itemView.context)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(radiusPx))
            .into(trackImage)
    }
}
