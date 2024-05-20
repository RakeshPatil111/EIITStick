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
import androidx.fragment.app.viewModels
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
import com.android.stickerpocket.utils.StickerViewModelFactory
import com.android.stickerpocket.utils.ViewExt.shakeMe
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GPHSearchGridCallback
import com.giphy.sdk.ui.views.GifView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class StickerFragment : Fragment(),
    StickerCategoryDialog.StickerCategoryDialogListener, EmojiPickerDialog.EmojiPickerDialogListener,
    GPHGridCallback, GPHSearchGridCallback, ItemTouchHelperAdapter, TextWatcher {

    private lateinit var binding: FragmentStickerBinding
    private lateinit var emojiCategoryListAdapter: EmojiCategoryListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var callback: ItemTouchHelperCallback
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    var searchJob: Job? = null

    private val interactor by lazy {
        StickerFragmentInteractor()
    }
    private val viewModel by viewModels<StickerViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStickerBinding.inflate(inflater, container, false)
        setClickListeners()
        interactor.initObserver(viewLifecycleOwner, viewModel)
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
                        removeChangeListeners(tietSearch)
                        tietSearch.setText(it.query)
                        tietSearch.setSelection(tietSearch.length())
                        if (rvStickers.content?.searchQuery != it.query) {
                            rvStickers.content = GPHContent.searchQuery(it.query, mediaType = MediaType.gif)
                        }
                        addChangeListeners(tietSearch)
                    }
                }
                is StickerFragmentInteractor.Actions.ShowRecentSearches -> {
                    recentSearchAdapter.updateList(it.recentSearches)
                    binding.rvRecentSearch.visibility = View.VISIBLE
                    binding.rvStickers.visibility = View.GONE
                }
                is StickerFragmentInteractor.Actions.LoadEmojisForCategory -> {
                    removeChangeListeners(binding.tietSearch)
                    binding.rvRecentSearch.visibility = View.GONE
                    binding.rvStickers.visibility = View.VISIBLE
                    binding.rvStickers.content = GPHContent.searchQuery(it.query)
                    binding.tietSearch.clearFocus()
                    binding.tietSearch.text?.clear()
                    addChangeListeners(binding.tietSearch)
                }
                is StickerFragmentInteractor.Actions.ShowCategoryOptionDialog -> {
                    if (!callback.isDragEnabled){
                        val stickerCategoryDialog = StickerCategoryDialog()
                        stickerCategoryDialog.setupDialogInformation(
                            listener = this
                        )
                        stickerCategoryDialog.show(childFragmentManager, "StickerCategoryDialog")
                    }
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

            override fun onRecentSearchRemove(position: Int) {
                interactor.onRecentSearchRemove(position)
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
            addChangeListeners(tietSearch)
        }
    }

    private fun addChangeListeners(tietSearch: TextInputEditText) {
        tietSearch.addTextChangedListener(this)
        tietSearch.setOnFocusChangeListener { v, hasFocus ->
            tietSearch.isCursorVisible = true
            if (hasFocus) {
                interactor.onSearchClick()
            } else {
                tietSearch.isCursorVisible = false
            }
        }
    }

    private fun removeChangeListeners(tietSearch: TextInputEditText) {
        tietSearch.removeTextChangedListener(this)
        tietSearch.setOnClickListener(null)
    }

    private fun setupEmojiRecyclerView() {
        emojiCategoryListAdapter = EmojiCategoryListAdapter()
        binding.rvCategory.adapter = emojiCategoryListAdapter

        callback = ItemTouchHelperCallback(this)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvCategory)

        emojiCategoryListAdapter.stickerActionClick { sticker, _ ->
            interactor.onCategoryItemClick(sticker)
        }
        emojiCategoryListAdapter.stickerActionLongClick { _, _ ->
            binding.tietSearch.isCursorVisible = false
            binding.tietSearch.text?.clear()
            interactor.onCategoryItemLongClick()
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

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        interactor.onSearchClick()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        interactor.onSearchClick()
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isEmpty()) {
            binding.rvRecentSearch.visibility = View.VISIBLE
            binding.rvStickers.visibility = View.GONE
            interactor.onQueryBlank()
        } else {
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(2500)
                s?.let {
                    if (it.toString().trim().isNotEmpty() && binding.rvStickers.content?.searchQuery != it.toString()) {
                        interactor.onQuerySearch(it.toString())
                    }
                }
            }
        }
    }
}