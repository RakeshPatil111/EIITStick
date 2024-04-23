package com.android.stickerpocket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.stickerpocket.databinding.FragmentSearchStickerBinding

class SearchStickerFragment : Fragment() {

    private lateinit var binding: FragmentSearchStickerBinding
    private lateinit var stickerSectionListAdapter: StickerSectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchStickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stickerSectionListAdapter = StickerSectionListAdapter()
        stickerSectionListAdapter.updateList(sectionStickers, requireActivity())
        binding.apply {
            rvStickerSection.adapter = stickerSectionListAdapter
        }
    }
}