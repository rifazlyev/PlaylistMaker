package com.example.playlistmaker.media.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistCardBinding
import com.example.playlistmaker.media.ui.model.PlaylistUi

class PlaylistAdapter(private val listener: OnPlaylistClickListener): RecyclerView.Adapter<PlaylistViewHolder>() {
    var listOfPlaylists: MutableList<PlaylistUi> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistCardBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listOfPlaylists.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(listOfPlaylists[position])
        holder.itemView.setOnClickListener {
            listener.onPlaylistClick(listOfPlaylists[position])
        }
    }
}
