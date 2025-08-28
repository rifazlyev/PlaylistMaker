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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.common.UiUtils.dpToPx
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {
    private val viewModel by viewModel<CreatePlaylistViewModel>()
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private var namePlaylistTextWatcher: TextWatcher? = null
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var uri: Uri? = null


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
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri == null) {
                    Toast.makeText(requireContext(), "Вы не выбрали фото", Toast.LENGTH_SHORT)
                        .show()
                    return@registerForActivityResult
                } else {
                    this.uri = uri
                    binding.playlistImageHint.setBackgroundResource(0)
                    val radiusPx = dpToPx(8F, requireContext())
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
            .setPositiveButton(getString(R.string.dialog_positive_button)) { _, _ -> findNavController().navigateUp() }

        namePlaylistTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                createPlaylistButtonEnabled(s.toString().isNotBlank())
            }
        }

        binding.nameEditText.addTextChangedListener(namePlaylistTextWatcher)
        binding.backButtonPlaylistScreen.setOnClickListener {
            checkScreenIsChanged()
        }

        binding.playlistImageHint.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylistButton.setOnClickListener {
            viewModel.createPlaylist(
                name = binding.nameEditText.text.toString(),
                description = binding.descriptionEditText.text.toString(),
                uri = uri
            )
            Toast.makeText(
                requireContext(),
                "Плейлист ${binding.nameEditText.text.toString()} создан",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            checkScreenIsChanged()
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
            findNavController().navigateUp()
        }
    }
}
