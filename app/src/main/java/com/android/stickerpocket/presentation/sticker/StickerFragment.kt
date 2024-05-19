package com.android.stickerpocket.presentation.sticker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.EmojiPickerDialog
import com.android.stickerpocket.databinding.FragmentStickerBinding
import com.android.stickerpocket.presentation.StickerCategoryDialog
import com.android.stickerpocket.presentation.StickerDialog
import com.android.stickerpocket.presentation.emoji
import com.android.stickerpocket.utils.GiphyConfigure
import com.android.stickerpocket.utils.ItemTouchHelperAdapter
import com.android.stickerpocket.utils.ItemTouchHelperCallback
import com.android.stickerpocket.utils.ViewExt.shakeMe
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GPHSearchGridCallback
import com.giphy.sdk.ui.views.GifView
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class StickerFragment : Fragment(),
    StickerCategoryDialog.StickerCategoryDialogListener,
    EmojiPickerDialog.EmojiPickerDialogListener, GPHGridCallback, GPHSearchGridCallback, ItemTouchHelperAdapter {

    private lateinit var binding: FragmentStickerBinding
    private lateinit var emojiCategoryListAdapter: EmojiCategoryListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var callback: ItemTouchHelperCallback
    private lateinit var recentSearchAdapter: RecentSearchAdapter

    private val interactor by lazy {
        StickerFragmentInteractor()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStickerBinding.inflate(inflater, container, false)
        setClickListeners()
        interactor.initObserver(viewLifecycleOwner)
        observeInteractor()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interactor.onViewCreated()
    }

    private fun observeInteractor() {
        interactor.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is StickerFragmentInteractor.Actions.InitGiphyView -> {
                    initGiphyView()
                }

                is StickerFragmentInteractor.Actions.InitCategoryView -> {
                    setupEmojiRecyclerView()
                    setupRecentSearchRecyclerView()
                }

                is StickerFragmentInteractor.Actions.HideGiphyGridViewAndShowRecentSearches -> {
                    binding.apply {
                        rvRecentSearch.visibility = View.VISIBLE
                        rvStickers.visibility = View.GONE
                    }
                }
                is StickerFragmentInteractor.Actions.ShowGiphyViewForRecentSearch -> {
                    binding.apply {
                        rvRecentSearch.visibility = View.GONE
                        rvStickers.visibility = View.VISIBLE
                        tietSearch.setText(it.query)
                        tietSearch.setSelection(tietSearch.length())
                        if (rvStickers.content?.searchQuery != it.query) {
                            rvStickers.content = GPHContent.searchQuery(it.query, mediaType = MediaType.gif)
                        }
                    }
                }
                is StickerFragmentInteractor.Actions.ShowRecentSearches -> {
                    recentSearchAdapter.updateList(it.recentSearches)
                    binding.rvRecentSearch.visibility = View.VISIBLE
                    binding.rvStickers.visibility = View.GONE
                }
                else -> {}
            }
        })
    }

    private fun setupRecentSearchRecyclerView() {
        recentSearchAdapter = RecentSearchAdapter()
        binding.rvRecentSearch.adapter = recentSearchAdapter
        recentSearchAdapter.setOnRecentSearchClickListener(object : RecentSearchAdapter.OnRecentSearchClickListener {
            override fun onRecentSearchClick(position: Int) {
                interactor.onRecentSearchItemClick(position)
            }
        })
    }

    private fun initGiphyView() {
        binding.rvStickers.apply {
            GiphyConfigure.configGiphyGridView(this)
            callback = this@StickerFragment
            content = GPHContent.recents
        }
    }

    private fun setClickListeners() {
        binding.apply {
            cvRecentSticker.setOnClickListener { binding.rvStickers.content = GPHContent.recents }
            cvFavSticker.setOnClickListener { binding.rvStickers.content = GPHContent.recents }
            cvDownloadedSticker.setOnClickListener { binding.rvStickers.content = GPHContent.recents }
            var searchJob: Job? = null
            tietSearch.doAfterTextChanged {
                if (it.toString().isEmpty()) {
                    rvRecentSearch.visibility = View.GONE
                    rvStickers.visibility = View.VISIBLE
                } else {
                    searchJob?.cancel()
                    searchJob = MainScope().launch {
                        delay(2500)
                        it?.let {
                            if (it.toString().trim().isNotEmpty() && rvStickers.content?.searchQuery != it.toString()) {
                                interactor.onQuerySearch(it.toString())
                            }
                        }
                    }
                }
            }

            tietSearch.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus && tietSearch.isCursorVisible) {
                    tietSearch.isCursorVisible = true
                    interactor.onSearchClick()
                } else {
                    tietSearch.isCursorVisible = false
                    rvStickers.content
                }
            }

            tietSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    tietSearch.isCursorVisible = true
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }
    }

    private fun setupEmojiRecyclerView() {
        emojiCategoryListAdapter = EmojiCategoryListAdapter()
        binding.rvCategory.adapter = emojiCategoryListAdapter

        callback = ItemTouchHelperCallback(this)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvCategory)

        emojiCategoryListAdapter.stickerActionClick { sticker, _ ->
            binding.tietSearch.setText("")
            binding.tietSearch.isCursorVisible = false
            binding.rvRecentSearch.visibility = View.GONE
            binding.rvStickers.visibility = View.VISIBLE
            binding.rvStickers.content = GPHContent.searchQuery(sticker.title.toString())
        }
        emojiCategoryListAdapter.stickerActionLongClick { _, _ ->
            binding.tietSearch.isCursorVisible = false
            binding.tietSearch.text?.clear()
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

    override fun onResume() {
        super.onResume()
        callback.isDragEnabled = false
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