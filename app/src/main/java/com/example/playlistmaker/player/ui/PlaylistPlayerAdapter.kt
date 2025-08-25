package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistPlayerCardBinding
import com.example.playlistmaker.media.ui.model.PlaylistUi
import com.example.playlistmaker.media.ui.playlist.OnPlaylistClickListener

class PlaylistPlayerAdapter(private val onPlaylistClickListener: OnPlaylistClickListener) :
    RecyclerView.Adapter<PlaylistPlayerViewHolder>() {
    var listOfPlaylists: MutableList<PlaylistUi> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistPlayerViewHolder {
        val binding =
            PlaylistPlayerCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistPlayerViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return listOfPlaylists.size
    }

    override fun onBindViewHolder(holder: PlaylistPlayerViewHolder, position: Int) {
        holder.bind(listOfPlaylists[position])
        holder.itemView.setOnClickListener {
            onPlaylistClickListener.onPlaylistClick(listOfPlaylists[position])
        }
    }
}
