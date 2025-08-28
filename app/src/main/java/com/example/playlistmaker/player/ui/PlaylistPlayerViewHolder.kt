package com.example.playlistmaker.player.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.UiUtils.dpToPx
import com.example.playlistmaker.databinding.PlaylistPlayerCardBinding
import com.example.playlistmaker.media.ui.model.PlaylistUi

class PlaylistPlayerViewHolder(private val playlistPlayerCardBinding: PlaylistPlayerCardBinding): RecyclerView.ViewHolder(playlistPlayerCardBinding.root) {
    fun bind(playlist: PlaylistUi){
        val res = playlistPlayerCardBinding.root.resources
        val count = playlist.tracksCount
        val text = res.getQuantityString(R.plurals.playlist_tracks_count, count, count)
        val radiusPx = dpToPx(2F, itemView.context)

        playlistPlayerCardBinding.playlistName.text = playlist.name
        playlistPlayerCardBinding.playlistTrackCount.text = text
        Glide.with(playlistPlayerCardBinding.root)
            .load(playlist.coverPath)
            .placeholder(R.drawable.track_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(radiusPx)
            )
            .into(playlistPlayerCardBinding.trackImage)
    }
}
