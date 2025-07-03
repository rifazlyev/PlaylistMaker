package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding

class FavoriteTracksFragment : Fragment() {
    companion object {
        fun newInstance(): Fragment {
            return FavoriteTracksFragment()
        }
    }

    private val viewModel: FavoriteTracksViewModel by viewModels()
    private var binding: FragmentFavoriteTracksBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
