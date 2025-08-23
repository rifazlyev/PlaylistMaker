package com.example.playlistmaker.media.ui.playlist

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
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreatePlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private var namePlaylistTextWatcher: TextWatcher? = null
    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var isPhotoSelected: Boolean = false

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
                } else {
                    binding.playlistImageHint.setImageURI(uri)
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
            showConfirmDialog()
        }

        binding.playlistImageHint.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showConfirmDialog()
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


}
