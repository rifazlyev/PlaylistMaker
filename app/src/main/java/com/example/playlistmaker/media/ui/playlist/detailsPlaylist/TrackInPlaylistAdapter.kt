package com.example.playlistmaker.media.ui.playlist.detailsPlaylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackCardBinding
import com.example.playlistmaker.search.ui.OnTrackClickListener
import com.example.playlistmaker.search.ui.TrackViewHolder
import com.example.playlistmaker.search.ui.model.TrackUi

class TrackInPlaylistAdapter(
    private val onTrackClickListener: OnTrackClickListener,
    private val onTrackInPlaylistLongClickListener: OnTrackInPlaylistLongClickListener
) : RecyclerView.Adapter<TrackViewHolder>() {
    val listOfTracks = mutableListOf<TrackUi>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return listOfTracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val trackUi = listOfTracks[position]
        holder.bind(trackUi)
        holder.itemView.setOnClickListener {
            onTrackClickListener.onTrackClick(trackUi)
        }
        holder.itemView.setOnLongClickListener {
            onTrackInPlaylistLongClickListener.onTrackLongClickListener(trackUi)
        }
    }
}
