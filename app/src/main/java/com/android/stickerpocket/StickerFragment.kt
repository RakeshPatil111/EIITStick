package com.android.stickerpocket

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.android.stickerpocket.databinding.FragmentStickerBinding
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHContentType
import com.giphy.sdk.ui.GiphyLoadingProvider
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GPHSearchGridCallback
import com.giphy.sdk.ui.views.GifView
import com.giphy.sdk.ui.views.GiphyGridView
import kotlinx.coroutines.delay
import timber.log.Timber

class StickerFragment : Fragment() {

    private lateinit var binding: FragmentStickerBinding
    private lateinit var emojiCategoryListAdapter: EmojiCategoryListAdapter
    private lateinit var gifListAdapter: GifListAdapter
    private lateinit var giphyGridView: GiphyGridView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStickers.apply {
            showViewOnGiphy = true
            spanCount = 3
            direction = GiphyGridView.VERTICAL
            showCheckeredBackground = true
            fixedSizeCells = true
            cellPadding = 24
        }
        setupEmojiRecyclerView()
        binding.rvStickers.callback = object : GPHGridCallback {
            override fun contentDidUpdate(resultCount: Int) {
                Timber.d("contentDidUpdate $resultCount")
            }

            override fun didSelectMedia(media: Media) {
                Timber.d("didSelectMedia ${media.id}")
                Toast.makeText(
                    requireContext(),
                    "media selected: ${media.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.rvStickers.searchCallback = object : GPHSearchGridCallback {
            override fun didTapUsername(username: String) {
                Timber.d("didTapUsername $username")
            }

            override fun didLongPressCell(cell: GifView) {
                Timber.d("didLongPressCell")
            }

            override fun didScroll(dx: Int, dy: Int) {
                Timber.d("didScroll")
            }
        }
        binding.rvStickers.content = GPHContent.recents
        binding.tietSearch.doAfterTextChanged {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    if (it.toString().isNotEmpty()) {
                        binding.rvStickers.content = GPHContent.searchQuery(it.toString())
                    }
                }, 1500
            )
        }
    }

    private fun setupEmojiRecyclerView() {
        emojiCategoryListAdapter = EmojiCategoryListAdapter()
        binding.rvEmoji.adapter = emojiCategoryListAdapter
        emojiCategoryListAdapter.stickerActionClick { sticker, _ ->
            binding.rvStickers.content = GPHContent.searchQuery(sticker.title)
        }
        emojiApiCallResponse()
    }
//
//    private fun setupGifsRecyclerView() {
//        gifListAdapter = GifListAdapter()
//        binding.rvStickers.adapter = gifListAdapter
//        gifListAdapter.gifActionClick { gif, _ ->
//            Log.d("selected gif", gif.toString())
//        }
//    }
//
    private fun emojiApiCallResponse() {
        emojiCategoryListAdapter.updateList(emoji)
    }
//
    private fun gifApiCallResponse() {
        //gifListAdapter.updateList(gifs, requireActivity(), "VERTICAL")
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