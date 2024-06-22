package com.android.stickerpocket.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvTutorials.setOnClickListener {
                findNavController().navigate(
                    R.id.action_settingsFragment_to_tutorialsFragment
                )
            }

            tvContactUs.setOnClickListener {
                findNavController().navigate(
                    R.id.action_settingsFragment_to_contactUsFragment
                )
            }

            tvDeletedStickers.setOnClickListener {
                findNavController().navigate(R.id.action_settingsFragment_to_deletedStickerFragment)
            }
        }
    }
}