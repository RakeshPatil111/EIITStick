package com.android.stickerpocket

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.databinding.FragmentStickerBinding
import com.android.stickerpocket.utils.ItemTouchHelperAdapter
import com.android.stickerpocket.utils.ItemTouchHelperCallback
import com.android.stickerpocket.utils.ViewExt.shakeMe
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GPHSearchGridCallback
import com.giphy.sdk.ui.views.GifView
import com.giphy.sdk.ui.views.GiphyGridView
import timber.log.Timber

class StickerFragment : Fragment(),
    StickerCategoryDialog.StickerCategoryDialogListener,
    EmojiPickerDialog.EmojiPickerDialogListener, GPHGridCallback, GPHSearchGridCallback, ItemTouchHelperAdapter {

    private lateinit var binding: FragmentStickerBinding
    private lateinit var emojiCategoryListAdapter: EmojiCategoryListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var callback: ItemTouchHelperCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStickerBinding.inflate(inflater, container, false)
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            cvRecentSticker.setOnClickListener { binding.rvStickers.content = GPHContent.recents }
            cvFavSticker.setOnClickListener { binding.rvStickers.content = GPHContent.recents }
            cvDownloadedSticker.setOnClickListener { binding.rvStickers.content = GPHContent.recents }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStickers.apply {
            showViewOnGiphy = false
            spanCount = 3
            direction = GiphyGridView.VERTICAL
            showCheckeredBackground = true
            fixedSizeCells = true
            cellPadding = 24
        }
        setupEmojiRecyclerView()
        binding.rvStickers.callback = this

        binding.rvStickers.content = GPHContent.recents
        binding.tietSearch.doAfterTextChanged {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    if (it.toString().isNotEmpty()) {
                        binding.rvStickers.content = GPHContent.searchQuery(it.toString(), mediaType = MediaType.gif)
                    }
                }, 1500
            )
        }
    }

    private fun setupEmojiRecyclerView() {
        emojiCategoryListAdapter = EmojiCategoryListAdapter()
        binding.rvCategory.adapter = emojiCategoryListAdapter

        callback = ItemTouchHelperCallback(this)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvCategory)

        emojiCategoryListAdapter.stickerActionClick { sticker, _ ->
            binding.rvStickers.content = GPHContent.searchQuery(sticker.title.toString())
        }
        emojiCategoryListAdapter.stickerActionLongClick { _, _ ->
            if (!callback.isDragEnabled){
                val stickerCategoryDialog = StickerCategoryDialog()
                stickerCategoryDialog.setupDialogInformation(
                    listener = this
                )
                stickerCategoryDialog.show(childFragmentManager, "StickerCategoryDialog")
            }
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
        callback.isDragEnabled = false
        Handler(Looper.getMainLooper()).postDelayed(
            {
                gifApiCallResponse()
            }, 500
        )
    }

    override fun addNewCategory() {
        val emojiPickerDialog = EmojiPickerDialog()
        emojiPickerDialog.setDialogListener(
            listener = this
        )
        emojiPickerDialog.show(childFragmentManager, "EmojiPickerDialog")
    }

    override fun reorganizeCategory() {
        applyShakeAnimation(binding.rvCategory)
        callback.isDragEnabled = true
    }

    private fun applyShakeAnimation(rvCategory: RecyclerView) {
        val layoutManager = rvCategory.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        for (i in firstVisiblePosition..lastVisiblePosition) {
            val viewHolder = rvCategory.findViewHolderForAdapterPosition(i)
            viewHolder?.itemView?.shakeMe()
        }
    }

    override fun deleteCategory() {

    }

    override fun addSelectedCategory(emojiItem: EmojiViewItem) {

    }

    override fun cancel() {
        Unit
    }
    override fun contentDidUpdate(resultCount: Int) {
        Timber.d("contentDidUpdate $resultCount")
    }

    override fun didSelectMedia(media: Media) {
        Timber.d("didSelectMedia ${media.id}")
        media.images.original?.gifUrl?.let {
            StickerDialog.show(childFragmentManager, it)
        }
    }

    override fun didLongPressCell(cell: GifView) {
        TODO("Not yet implemented")
    }

    override fun didScroll(dx: Int, dy: Int) {
        TODO("Not yet implemented")
    }

    override fun didTapUsername(username: String) {
        TODO("Not yet implemented")
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        emojiCategoryListAdapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onDragComplete() {
        callback.isDragEnabled = false
    }
}