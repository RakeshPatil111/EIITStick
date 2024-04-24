package com.android.stickerpocket

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.stickerpocket.databinding.FragmentStickerBinding

class StickerFragment : Fragment() {

    private lateinit var binding: FragmentStickerBinding
    private lateinit var emojiCategoryListAdapter: EmojiCategoryListAdapter
    private lateinit var gifListAdapter: GifListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEmojiRecyclerView()
        setupGifsRecyclerView()
        emojiApiCallResponse()
    }

    private fun setupEmojiRecyclerView() {
        emojiCategoryListAdapter = EmojiCategoryListAdapter()
        binding.rvEmoji.adapter = emojiCategoryListAdapter
        emojiCategoryListAdapter.stickerActionClick { sticker, _ ->
            Log.d("selected emoji", sticker.toString())
            gifApiCallResponse()
        }
    }

    private fun setupGifsRecyclerView() {
        gifListAdapter = GifListAdapter()
        binding.rvStickers.adapter = gifListAdapter
        gifListAdapter.gifActionClick { gif, _ ->
            Log.d("selected gif", gif.toString())
        }
    }

    private fun emojiApiCallResponse() {
        emojiCategoryListAdapter.updateList(emoji)
    }

    private fun gifApiCallResponse() {
        gifListAdapter.updateList(gifs, requireActivity(), "VERTICAL")
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                gifApiCallResponse()
            }, 500
        )
    }
}