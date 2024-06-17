package com.android.stickerpocket.presentation.sticker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.BuildConfig
import com.android.stickerpocket.EmojiPickerDialog
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.FragmentStickerBinding
import com.android.stickerpocket.presentation.CommonStickerAdapter
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.presentation.FavouritesAdapter
import com.android.stickerpocket.presentation.StickerCategoryDialog
import com.android.stickerpocket.presentation.StickerDetailsNavDirections
import com.android.stickerpocket.presentation.StickerDialog
import com.android.stickerpocket.utils.ItemTouchHelperAdapter
import com.android.stickerpocket.utils.ItemTouchHelperCallback
import com.android.stickerpocket.utils.StickerExt.toStickerDTO
import com.android.stickerpocket.utils.ViewExt.removeBorder
import com.android.stickerpocket.utils.ViewExt.setBorder
import com.android.stickerpocket.utils.ViewExt.shakeMe
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GPHSearchGridCallback
import com.giphy.sdk.ui.views.GifView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class StickerFragment : Fragment(), GPHGridCallback,
        ItemTouchHelperAdapter, TextWatcher {
    private lateinit var binding: FragmentStickerBinding
    private lateinit var emojiCategoryListAdapter: EmojiCategoryListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var stickerItemTouchHelper: ItemTouchHelper
    private lateinit var callback: ItemTouchHelperCallback
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var commonStickerAdapter: CommonStickerAdapter
    private lateinit var favouritesAdapter: FavouritesAdapter
    var searchJob: Job? = null
    private lateinit var currentRecyclerView: RecyclerView

    private val interactor by lazy {
        StickerFragmentInteractor()
    }
    private val viewModel by activityViewModels<StickerViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStickerBinding.inflate(inflater, container, false)
        interactor.initObserver(viewLifecycleOwner, viewModel)
        observeInteractor()
        setClickListeners()
        initAdapters()
        handleBackPress()
        return binding.root
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if ( requireActivity().currentFocus != null) {
                    requireActivity().currentFocus?.let {
                        if (it.id == R.id.tiet_search) {
                            removeChangeListeners(binding.tietSearch)
                            binding.tietSearch.clearFocus()
                            binding.tietSearch.text?.clear()
                            binding.rvRecentSearch.visibility = View.GONE
                            currentRecyclerView.visibility = View.VISIBLE
                        } else {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }
                } else {
                    requireActivity().finish()
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interactor.onViewCreated()
    }

    private fun observeInteractor() {
        interactor.liveData.observe(viewLifecycleOwner, Observer {
            when (val action = it.getContentIfNotHandled()) {
                is StickerFragmentInteractor.Actions.InitCategoryView -> {
                    setupEmojiRecyclerView(action.categories)
                }

                is StickerFragmentInteractor.Actions.HideGiphyGridViewAndShowRecentSearches -> {
                    binding.apply {
                        rvRecentSearch.visibility = View.VISIBLE
                        rvStickers.visibility = View.GONE
                    }
                }

                is StickerFragmentInteractor.Actions.ShowStickerForRecentSearch -> {
                    binding.apply {
                        rvRecentSearch.visibility = View.GONE
                        rvStickers.visibility = View.VISIBLE
                        rvStickers.adapter = commonStickerAdapter
                        commonStickerAdapter.updateList(action.stickers)
                        removeChangeListeners(tietSearch)
                        tietSearch.setText(action.query)
                        tietSearch.setSelection(tietSearch.length())
                        addChangeListeners(tietSearch)
                        currentRecyclerView = rvStickers
                        commonStickerAdapter.isOpenedForCategory(false)
                    }
                }

                is StickerFragmentInteractor.Actions.ShowRecentSearches -> {
                    recentSearchAdapter.updateList(action.recentSearches)
                    binding.rvRecentSearch.visibility = View.VISIBLE
                    binding.rvStickers.visibility = View.GONE
                }

                is StickerFragmentInteractor.Actions.LoadEmojisForCategory -> {
                    removeChangeListeners(binding.tietSearch)
                    binding.rvRecentSearch.visibility = View.GONE
                    binding.rvStickers.visibility = View.VISIBLE
                    binding.tietSearch.clearFocus()
                    binding.tietSearch.text?.clear()
                    addChangeListeners(binding.tietSearch)
                    currentRecyclerView = binding.rvStickers
                }

                is StickerFragmentInteractor.Actions.ShowCategoryOptionDialog -> {
                    if (!callback.isDragEnabled) {
                        val stickerCategoryDialog = StickerCategoryDialog()
                        stickerCategoryDialog.setupDialogInformation(
                            object : StickerCategoryDialog.StickerCategoryDialogListener {
                                override fun onAddNewCategory() {
                                    val emojiPickerDialog = EmojiPickerDialog()
                                    emojiPickerDialog.setDialogListener(
                                        object : EmojiPickerDialog.EmojiPickerDialogListener {
                                            override fun addSelectedCategory(emojiItem: EmojiViewItem) {
                                                interactor.onAddNewCategory(
                                                    emojiItem,
                                                    action.category,
                                                    action.pos,
                                                    action.previous
                                                )
                                            }

                                            override fun cancel() {
                                                emojiPickerDialog.dismiss()
                                            }

                                        }
                                    )
                                    emojiPickerDialog.show(
                                        childFragmentManager,
                                        "EmojiPickerDialog"
                                    )
                                }

                                override fun onReorganize() {
                                    applyShakeAnimation(binding.rvCategory)
                                    callback.isDragEnabled = true
                                }

                                override fun onDelete() {
                                    interactor.onDeleteCategory(
                                        category = action.category,
                                        pos = action.pos
                                    )
                                }

                                override fun onCancel() {
                                    stickerCategoryDialog.dismiss()
                                }

                            }
                        )
                        stickerCategoryDialog.show(
                            childFragmentManager,
                            "StickerCategoryDialog"
                        )
                    }
                }

                is StickerFragmentInteractor.Actions.ShowStickerDialog -> {
                    val stickerDialog = StickerDialog()
                    stickerDialog.setSticker(action.sticker)
                    stickerDialog.isOpenedForFav(action.isFavourite)
                    stickerDialog.setListener(object : StickerDialog.StickerDialogListener {
                        override fun onStickerInfoClick(sticker: Sticker) {
                            interactor.onStickerInfoClick(sticker)
                        }

                        override fun onShareSticker(sticker: Sticker) {
                            interactor.onStickerShare(sticker)
                        }

                        override fun onAddStickerToFavoritesClick(sticker: Sticker, didOpenForFav: Boolean) {
                            interactor.onAddStickerToFavoritesClick(sticker, didOpenForFav)
                        }

                        override fun onCancelClick() {
                            stickerDialog.dismiss()
                        }

                    })
                    stickerDialog.show(childFragmentManager, "StickerDialog")
                }

                is StickerFragmentInteractor.Actions.NavigateToStickerInfo -> {
                    val direction = StickerDetailsNavDirections(action.stickerDTO)
                    findNavController().navigate(direction)
                }

                is StickerFragmentInteractor.Actions.ShowFavoritesSticker -> {
                    if (!emojiCategoryListAdapter.isCategorySelected()) {
                        binding.apply {
                            setStaticPagesBorder(action)
                            rvRecentSearch.visibility = View.GONE
                            rvStickers.visibility = View.VISIBLE
                            rvStickers.adapter = favouritesAdapter
                            favouritesAdapter.updateList(action.favoriteStickers)
                            binding.apply {
                                cvFavSticker.setBorder()
                                cvDownloadedSticker.removeBorder()
                                cvRecentSticker.removeBorder()
                            }
                        }
                    }
                }

                is StickerFragmentInteractor.Actions.ReloadCategories -> {
                    emojiCategoryListAdapter.updateList(action.categories)
                }
                is StickerFragmentInteractor.Actions.ShowMessage -> {
                    Toast.makeText(requireContext(), action.message, Toast.LENGTH_SHORT).show()
                }
                is StickerFragmentInteractor.Actions.clearAllRecentSearchAndHideView ->{
                    binding.apply {
                        rvRecentSearch.visibility = View.GONE
                        rvStickers.visibility = View.VISIBLE
                    }
                }

                is StickerFragmentInteractor.Actions.ShowStickers -> {
                    commonStickerAdapter.updateList(action.stickers)
                    if (emojiCategoryListAdapter.isCategorySelected()) {
                        binding.rvStickers.visibility = View.VISIBLE
                        binding.rvStickers.adapter = commonStickerAdapter
                        currentRecyclerView = binding.rvStickers
                        commonStickerAdapter.isOpenedForCategory(true)
                    }
                }

                is StickerFragmentInteractor.Actions.ShowDownloadedStickers -> {
                    commonStickerAdapter.updateList(action.stickers)
                    commonStickerAdapter.isOpenedForCategory(false)
                    binding.rvStickers.visibility = View.VISIBLE
                    binding.rvStickers.adapter = commonStickerAdapter
                    currentRecyclerView = binding.rvStickers
                }

                is StickerFragmentInteractor.Actions.ShowRecentStickers -> {
                    commonStickerAdapter.updateList(action.stickers)
                    commonStickerAdapter.isOpenedForCategory(false)
                    binding.rvStickers.adapter = commonStickerAdapter
                    currentRecyclerView = binding.rvStickers
                }
                else -> {}
            }
        })
    }

    private fun setStaticPagesBorder(action: StickerFragmentInteractor.Actions) {
        emojiCategoryListAdapter.clearSelection()
    }

    private fun setupRecentSearchRecyclerView() {
        recentSearchAdapter = RecentSearchAdapter()
        binding.rvRecentSearch.adapter = recentSearchAdapter
        recentSearchAdapter.setOnRecentSearchClickListener(object :
            RecentSearchAdapter.OnRecentSearchClickListener {
            override fun onRecentSearchClick(position: Int) {
                interactor.onRecentSearchItemClick(position)
            }

            override fun onRecentSearchRemove(position: Int) {
                interactor.onRecentSearchRemove(position)
            }

            override fun onClearRecentSearchClick() {
                interactor.onClearAllRecentSearch()
            }
        })
    }

    private fun initAdapters() {
        commonStickerAdapter = CommonStickerAdapter()
        favouritesAdapter = FavouritesAdapter()
        binding.rvStickers.adapter = commonStickerAdapter
        binding.rvStickers.visibility = View.VISIBLE
        currentRecyclerView = binding.rvStickers
        setupRecentSearchRecyclerView()
        commonStickerAdapter.onItemClick { sticker, position ->
            interactor.onStickerClick(sticker, position)
        }

        favouritesAdapter.onItemClick { sticker, position ->
            interactor.onFavStickerClick(sticker, position)
        }
    }

    private fun setClickListeners() {
        binding.apply {
            cvRecentSticker.setOnClickListener {
                rvRecentSearch.visibility = View.GONE
                rvStickers.visibility = View.VISIBLE
                emojiCategoryListAdapter.clearSelection()
                cvFavSticker.removeBorder()
                cvDownloadedSticker.removeBorder()
                cvRecentSticker.setBorder()
                interactor.onRecentStickerClick()

            }
            cvFavSticker.setOnClickListener {
                interactor.onFavClick()
                emojiCategoryListAdapter.clearSelection()
                cvFavSticker.setBorder()
                cvDownloadedSticker.removeBorder()
                cvRecentSticker.removeBorder()
            }
            cvDownloadedSticker.setOnClickListener {
                rvRecentSearch.visibility = View.GONE
                rvStickers.visibility = View.VISIBLE
                emojiCategoryListAdapter.clearSelection()
                interactor.onDownloadClick()
                cvFavSticker.removeBorder()
                cvDownloadedSticker.setBorder()
                cvRecentSticker.removeBorder()
            }
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

    private fun setupEmojiRecyclerView(categories: List<Category>) {
        emojiCategoryListAdapter = EmojiCategoryListAdapter()
        binding.rvCategory.adapter = emojiCategoryListAdapter
        callback = ItemTouchHelperCallback(this)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvCategory)
        stickerItemTouchHelper = ItemTouchHelper(object :ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = if ((recyclerView.adapter as CommonStickerAdapter).didOpenForCategory) {
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                } else 0
                val swipeFlags = 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                interactor.onStickerMoved(viewHolder.absoluteAdapterPosition, target.absoluteAdapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // No action required as swipe is disabled
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                interactor.onStickerDragComplete()
            }

        })
        stickerItemTouchHelper.attachToRecyclerView(binding.rvStickers)

        emojiCategoryListAdapter.stickerActionClick { sticker, _, previous ->
            interactor.onCategoryItemClick(sticker, previous)
            clearStaticPagesBorder()
        }
        emojiCategoryListAdapter.stickerActionLongClick { category, pos, previous ->
            binding.tietSearch.isCursorVisible = false
            binding.tietSearch.text?.clear()
            interactor.onCategoryItemLongClick(category, pos, previous)
        }
        emojiCategoryListAdapter.updateList(categories)
    }

    private fun clearStaticPagesBorder() {
        binding.apply {
            cvFavSticker.removeBorder()
            cvRecentSticker.removeBorder()
            cvDownloadedSticker.removeBorder()
        }
    }

    override fun onResume() {
        super.onResume()
        callback.isDragEnabled = false
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

    override fun contentDidUpdate(resultCount: Int) {
        Timber.d("contentDidUpdate $resultCount")
    }

    override fun didSelectMedia(media: Media) {
        media.images.original?.gifUrl?.let {
            interactor.onMediaClick(media)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        emojiCategoryListAdapter.notifyItemMoved(fromPosition, toPosition)
        interactor.onItemMove(fromPosition, toPosition)
    }

    override fun onDragComplete() {
        callback.isDragEnabled = false
        interactor.onDragComplete()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (binding.tietSearch.hasFocus()) {
            interactor.onSearchClick()
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().isEmpty() && binding.tietSearch.hasFocus()) {
            binding.rvRecentSearch.visibility = View.VISIBLE
            binding.rvStickers.visibility = View.GONE
            interactor.onQueryBlank()
        } else {
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(2500)
                s?.let {
                    if (it.toString().trim()
                            .isNotEmpty()) {
                        interactor.onQuerySearch(it.toString())
                    }
                }
            }
        }
    }
}