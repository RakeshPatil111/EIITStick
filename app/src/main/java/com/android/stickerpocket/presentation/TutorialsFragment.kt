package com.android.stickerpocket.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.stickerpocket.databinding.FragmentTutorialsBinding
import com.android.stickerpocket.utils.AppConstants

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
        binding.apply {

        }
    }

    fun onTutorialClick(view: View) {
        when (view.tag) {
            AppConstants.SEND_STICKER_FROM_APP -> {
                // Handle click for SEND_STICKER_FROM_APP
            }
            AppConstants.SEND_STICKER_FROM_KEYBOARD -> {
                // Handle click for SEND_STICKER_FROM_KEYBOARD
            }
            AppConstants.OPEN_STICKER_POCKET_KEYBOARD -> {
                // Handle click for OPEN_STICKER_POCKET_KEYBOARD
            }
            AppConstants.SETUP_STICKER_POCKET_KEYBOARD -> {
                // Handle click for SETUP_STICKER_POCKET_KEYBOARD
            }
            AppConstants.DOWNLOAD_STICKER -> {
                // Handle click for DOWNLOAD_STICKER
            }
            AppConstants.MANAGE_STICKERS -> {
                // Handle click for MANAGE_STICKERS
            }
            AppConstants.ADD_TAG_TO_STICKER -> {
                // Handle click for ADD_TAG_TO_STICKER
            }
            AppConstants.REORDER_STICKERS -> {
                // Handle click for REORDER_STICKERS
            }
            AppConstants.MOVE_STICKER_TO_NEW_POCKET -> {
                // Handle click for MOVE_STICKER_TO_NEW_POCKET
            }
            AppConstants.MOVE_MULTIPLE_STICKER_TO_NEW_POCKET -> {
                // Handle click for MOVE_MULTIPLE_STICKER_TO_NEW_POCKET
            }
            AppConstants.MANAGE_POCKETS -> {
                // Handle click for MANAGE_POCKETS
            }
            AppConstants.REORGANIZE_POCKET -> {
                // Handle click for REORGANIZE_POCKET
            }
            AppConstants.WELCOME_MESSAGE -> {
                // Handle click for WELCOME_MESSAGE
            }
            AppConstants.STICKER_POCKET_LAYOUT -> {
                // Handle click for STICKER_POCKET_LAYOUT
            }
            AppConstants.IMPORT_STICKER_FROM_WHATSAPP -> {
                // Handle click for IMPORT_STICKER_FROM_WHATSAPP
            }
            AppConstants.IMPORT_STICKER_FROM_PHOTO -> {
                // Handle click for IMPORT_STICKER_FROM_PHOTO
            }
            else -> {
                // Default case, handle unknown tag
            }
        }
    }

}