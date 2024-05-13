package com.android.stickerpocket

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.android.stickerpocket.databinding.FragmentStickerBinding
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GiphyGridView
import timber.log.Timber

class StickerFragment : Fragment(), StickerDialog.StickerDialogListener {

    private lateinit var binding: FragmentStickerBinding
    private lateinit var emojiCategoryListAdapter: EmojiCategoryListAdapter

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
                /*Toast.makeText(
                    requireContext(),
                    "media selected: ${media.id}",
                    Toast.LENGTH_SHORT
                ).show()*/
                StickerDialog.show(childFragmentManager, "https://i.ibb.co/6BH61RN/first.gif")
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
            binding.rvStickers.content = GPHContent.searchQuery(sticker.title.toString())
        }
        emojiCategoryListAdapter.stickerActionLongClick { sticker, position ->
            val view = binding.rvEmoji.layoutManager?.findViewByPosition(position)
            val location = IntArray(2)
            view?.getLocationOnScreen(location)
            val x = location[0]
            val y = location[1]
            println("Item $position clicked, X: $x, Y: $y")
            StickerCategoryDialog.show(childFragmentManager, sticker, x, y)
        }

        emojiApiCallResponse()
    }

    private fun emojiApiCallResponse() {
        emojiCategoryListAdapter.updateList(emoji)
    }

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

    override fun selectedSticker(sticker: Sticker) {
        //val action = StickerDetailsNavDirections(sticker)
        //findNavController().navigate(action)
    }

    override fun cancel() {
        Unit
    }
}