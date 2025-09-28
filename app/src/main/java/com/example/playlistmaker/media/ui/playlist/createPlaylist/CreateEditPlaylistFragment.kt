package com.example.playlistmaker.media.ui.playlist.createPlaylist

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.UiUtils.dpToPx
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.ui.model.PlaylistUi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateEditPlaylistFragment : Fragment() {
    private val viewModel by viewModel<CreateEditPlaylistViewModel>()
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private var namePlaylistTextWatcher: TextWatcher? = null
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var uri: Uri? = null
    private var playlistId: Long? = null
    private val radiusPx: Int by lazy { dpToPx(8F, requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        playlistId = if (args?.containsKey(PLAYLIST_ID) == true) args.getLong(PLAYLIST_ID) else null
        viewModel.setModeFrom(playlistId)
        viewModel.observePlaylistConditionUiState().observe(viewLifecycleOwner) { uiState ->
            renderState(uiState)
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri == null) {
                    Toast.makeText(requireContext(), "Вы не выбрали фото", Toast.LENGTH_SHORT)
                        .show()
                    return@registerForActivityResult
                } else {
                    this.uri = uri
                    binding.playlistImageHint.setBackgroundResource(0)
                    Glide.with(binding.root)
                        .load(uri)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(radiusPx)
                        )
                        .into(binding.playlistImageHint)
                }
            }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setNegativeButton(getString(R.string.dialog_negative_button), null)
            .setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ -> closeScreen() }

        namePlaylistTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                createPlaylistButtonEnabled(s.toString().isNotBlank())
            }
        }

        binding.nameEditText.addTextChangedListener(namePlaylistTextWatcher)
        binding.backButtonPlaylistScreen.setOnClickListener {
            onBackPressedLogic()
        }

        binding.playlistImageHint.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylistButton.setOnClickListener {
            onSavePlaylistLogic(playlistId)


        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressedLogic()
        }
    }

    private fun createPlaylistButtonEnabled(boolean: Boolean) {
        binding.createPlaylistButton.isEnabled = boolean
    }

    private fun showConfirmDialog() {
        val dialog = confirmDialog.create()
        dialog.show()
        //хочу сделать согласованно системе, поэтому крашу кнопки в синий
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
    }

    private fun checkScreenIsChanged() {
        if (uri != null ||
            !binding.nameEditText.text.isNullOrEmpty() || !binding.descriptionEditText.text.isNullOrEmpty()
        ) showConfirmDialog()
        else {
            closeScreen()
        }
    }

    private fun onBackPressedLogic() {
        if (playlistId != null) {
            closeScreen()
        } else {
            checkScreenIsChanged()
        }
    }

    private fun onSavePlaylistLogic(playlistId: Long?) {
        val name = binding.nameEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val uri = uri

        when (playlistId) {
            null -> createPlaylist(name, description, uri)
            else -> editPlaylist(name, description, uri)
        }
    }

    private fun createPlaylist(name: String, description: String, uri: Uri?) {
        viewModel.createPlaylist(
            name = name,
            description = description,
            uri = uri
        )
        Toast.makeText(
            requireContext(),
            "Плейлист ${binding.nameEditText.text.toString()} создан",
            Toast.LENGTH_SHORT
        ).show()
        closeScreen()
    }

    private fun editPlaylist(name: String, description: String, uri: Uri?) {
        viewModel.editPlaylist(name = name, description = description, uri = uri)
        closeScreen()
    }


    private fun renderState(playlistState: PlaylistConditionUiState) {
        when (playlistState) {
            is PlaylistConditionUiState.Creation -> renderCreationScreen()
            is PlaylistConditionUiState.Editing -> renderEditingScreen(playlistState.playlist)
        }
    }

    private fun renderCreationScreen() {
        binding.screenTitle.setText(R.string.new_playlist_title)
        binding.createPlaylistButton.setText(R.string.create_playlist)
    }

    private fun renderEditingScreen(playlist: PlaylistUi) {
        binding.screenTitle.setText(R.string.edit_playlist_title)
        binding.createPlaylistButton.text = getString(R.string.save_playlist)
        Glide.with(this)
            .load(playlist.coverPath)
            .placeholder(R.drawable.ic_placeholder_player_screen)
            .transform(
                CenterCrop(),
                RoundedCorners(radiusPx)
            )
            .into(binding.playlistImageHint)
        binding.nameEditText.setText(playlist.name)
        binding.descriptionEditText.setText(playlist.description)
    }

    private fun closeScreen() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.nameEditText.removeTextChangedListener(namePlaylistTextWatcher)
        _binding = null
        namePlaylistTextWatcher = null
    }

    companion object {
        private const val PLAYLIST_ID = "playlist"
        fun createArg(playlistId: Long) = bundleOf(PLAYLIST_ID to playlistId)
    }
}
