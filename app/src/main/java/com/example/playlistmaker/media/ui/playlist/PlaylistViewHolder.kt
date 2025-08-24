package com.example.playlistmaker.media.ui.playlist

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.UiUtils.dpToPx
import com.example.playlistmaker.databinding.PlaylistCardBinding
import com.example.playlistmaker.media.ui.model.PlaylistUi
import com.bumptech.glide.load.resource.bitmap.CenterCrop

class PlaylistViewHolder(private val playlistCardBinding: PlaylistCardBinding) :
    RecyclerView.ViewHolder(playlistCardBinding.root) {

    fun bind(playlist: PlaylistUi) {
        val res = playlistCardBinding.root.resources
        val count = playlist.tracksCount
        val text = res.getQuantityString(R.plurals.playlist_tracks_count, count, count)
        val radiusPx = dpToPx(8F, itemView.context)

        playlistCardBinding.playlistName.text = playlist.name
        playlistCardBinding.playlistTracksCount.text = text
        Glide.with(playlistCardBinding.root)
            .load(playlist.coverPath)
            .placeholder(R.drawable.track_placeholder)
            .transform(
            CenterCrop(),
            RoundedCorners(radiusPx)
        )
            .into(playlistCardBinding.playlistImage)
    }
}
