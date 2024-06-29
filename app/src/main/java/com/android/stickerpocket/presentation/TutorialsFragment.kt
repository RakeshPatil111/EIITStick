package com.android.stickerpocket.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.FragmentTutorialsBinding
import com.android.stickerpocket.utils.tutorials
import com.google.android.material.textview.MaterialTextView

class TutorialsFragment : Fragment() {

    private lateinit var binding: FragmentTutorialsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        binding.apply {
            tvWelcomeMessage.setOnClickListener {
                showWelcomeDialog()
            }
            tvImportStickersFromWhatsapp.setOnClickListener {

            }
            tvImportStickersFromPhoto.setOnClickListener {

            }
            tvStickerPocketLayout.setOnClickListener {

            }
        }
    }

    private fun showWelcomeDialog() {

    }

    private fun setupClickListeners() {
        val textViewIds = listOf(
            R.id.tv_send_sticker_from_app,
            R.id.tv_send_sticker_from_keyboard,
            R.id.tv_open_sticker_pocket_keyboard,
            R.id.tv_setup_sticker_pocket_keyboard,
            R.id.tv_download_stickers,
            R.id.tv_manage_stickers,
            R.id.tv_add_tags_to_sticker,
            R.id.tv_reorder_stickers,
            R.id.tv_move_sticker_to_new_pocket,
            R.id.tv_move_multiple_stickers_to_new_pocket,
            R.id.tv_manage_pockets,
            R.id.tv_reorganize_pockets,
        )

        textViewIds.forEach { id ->
            val textView = binding.root.findViewById<MaterialTextView>(id)
            if (textView != null) {
                textView.setOnClickListener { view -> onTutorialClick(view) }
            } else {
                Log.e("TutorialsFragment", "TextView with ID $id not found")
            }
        }
    }

    private fun onTutorialClick(view: View) {
        val tag = view.tag as String
        val selectedTutorial = tutorials.find { it.tag == tag }
        selectedTutorial?.let {
            Log.d("tut click tag", it.tag)
            Log.d("tut click title ", "details : ${it.title}")
            Log.d("tut click gif ","details : ${it.gif}")
            val action =
                TutorialsFragmentDirections.actionTutorialsFragmentToTutorialDetailsFragment(it)
            findNavController().navigate(action)
        }
    }

}