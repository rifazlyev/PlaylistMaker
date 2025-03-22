package com.example.playlistmaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.listeners.OnTrackClickListener
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.viewHolder.TrackViewHolder
import java.util.ArrayList

class TrackAdapter(private val listener: OnTrackClickListener? = null) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var trackList: ArrayList<Track> = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_card, parent, false)
        return TrackViewHolder(view)

    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            listener?.onTrackClick(trackList[position])
        }
    }
}