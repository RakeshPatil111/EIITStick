package com.android.stickerpocket.presentation.sticker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.CommunicationBridge
import com.android.stickerpocket.EmojiPickerDialog
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.FragmentStickerBinding
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.presentation.CommonStickerAdapter
import com.android.stickerpocket.presentation.FavouritesAdapter
import com.android.stickerpocket.presentation.StickerCategoryDialog
import com.android.stickerpocket.presentation.StickerDetailsNavDirections
import com.android.stickerpocket.presentation.StickerDialog
import com.android.stickerpocket.utils.CustomDialog
import com.android.stickerpocket.utils.ItemTouchHelperAdapter
import com.android.stickerpocket.utils.ItemTouchHelperCallback
import com.android.stickerpocket.utils.OnItemDoubleClickListener
import com.android.stickerpocket.utils.ViewExt.removeBorder
import com.android.stickerpocket.utils.ViewExt.setBorder
import com.android.stickerpocket.utils.ViewExt.shakeMe
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.views.GPHGridCallback
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
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

        binding.apply{
            tietSearch.setOnEditorActionListener { view, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    val query = view.text.toString()
                    hideKeyboard()
                    if (query.isEmpty() && tietSearch.hasFocus()) {
                        rvRecentSearch.visibility = View.VISIBLE
                        rvStickers.visibility = View.GONE
                        interactor.onQueryBlank()
                    } else {
                        searchJob?.cancel()
                        searchJob = MainScope().launch {
                            query.let {
                                if (it.trim()
                                        .isNotEmpty()) {
                                    interactor.onQuerySearch(it)
                                }
                            }
                        }
                    }
                    true
                } else {
                    false
                }
            }

            tilSearch.setEndIconOnClickListener {
                if(tietSearch.text.isNullOrEmpty()){
                    hideKeyboard()
                    removeChangeListeners(tietSearch)
                    tietSearch.clearFocus()
                    tietSearch.text?.clear()
                    addChangeListeners(tietSearch)
                    rvRecentSearch.visibility = View.GONE
                    currentRecyclerView = rvStickers
                    interactor.onEditTextClear()
                    currentRecyclerView.visibility = View.VISIBLE
                } else {
                    tietSearch.text?.clear()
                }
            }
        }
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
                    if (action.categories.isNotEmpty()) {
                        emojiCategoryListAdapter.updateList(action.categories)
                    }
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
                        hideKeyboard()
                        if (action.stickers.isEmpty()){
                            CustomDialog.showCustomDialog(requireContext(),resources.getString(R.string.no_stickers_found),resources.getString(R.string.ok))
                            CustomDialog.alertDialog.setOnDismissListener {
                                rvRecentSearch.visibility = View.GONE
                                rvStickers.visibility = View.VISIBLE
                                removeChangeListeners(tietSearch)
                                binding.tietSearch.clearFocus()
                                binding.tietSearch.text?.clear()
                            }
                        }else{
                            removeChangeListeners(tietSearch)
                            rvStickers.adapter = commonStickerAdapter
                            commonStickerAdapter.updateList(action.stickers)
                            tietSearch.setText(action.query)
                            tietSearch.setSelection(tietSearch.length())
                            addChangeListeners(tietSearch)
                            currentRecyclerView = rvStickers
                            commonStickerAdapter.isOpenedForCategory(false)
                            //commonStickerAdapter.isOpenedForOrganizeCategory(false)
                            exitSelectionMode()
                        }
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

                        override fun onAddStickerToDeletedClick(sticker: Sticker, didOpenForFav: Boolean) {
                            interactor.onAddStickerToDeletedClick(sticker, didOpenForFav)
                        }

                        override fun onReOrganizeClick() {
                            stickerDialog.dismiss()
                            CommunicationBridge.isOrganizationMode.value=true
                            commonStickerAdapter.notifyDataSetChanged()
                            binding.btnSelect.visibility=View.VISIBLE
                            binding.btnCancel.visibility=View.VISIBLE
                            applyShakeAnimation(binding.rvStickers)
                        }

                        override fun onCancelClick() {
                            stickerDialog.dismiss()
                        }

                    })
                    if (CommunicationBridge.isOrganizationMode.value==false){
                        stickerDialog.show(childFragmentManager, "StickerDialog")
                    }

                }

                is StickerFragmentInteractor.Actions.ShareSticker -> {

                    interactor.onStickerShare(action.sticker)
                }

                is StickerFragmentInteractor.Actions.NavigateToStickerInfo -> {
                    val direction = StickerDetailsNavDirections(action.stickerDTO)
                    findNavController().navigate(direction)
                }

                is StickerFragmentInteractor.Actions.ShowFavoritesSticker -> {

                    if (CommunicationBridge.isOrganizationMode.value==true){
                        exitSelectionMode()
                    }else {
                        if (!emojiCategoryListAdapter.isCategorySelected()) {
                            binding.apply {
                                setStaticPagesBorder(action)
                                rvRecentSearch.visibility = View.GONE
                                rvStickers.visibility = View.VISIBLE
                                binding.btnSelect.visibility=View.GONE
                                binding.btnCancel.visibility=View.GONE
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
                }

                is StickerFragmentInteractor.Actions.ReloadCategories -> {
                    emojiCategoryListAdapter.updateList(action.categories)
                }
                is StickerFragmentInteractor.Actions.ShowMessage -> {
                    Toast.makeText(requireContext(), action.message, Toast.LENGTH_SHORT).show()
                }
                is StickerFragmentInteractor.Actions.clearAllRecentSearchAndHideView ->{
                    hideKeyboard()
                    binding.apply {
                        rvRecentSearch.visibility = View.GONE
                        rvStickers.visibility = View.VISIBLE
                    }
                }

                is StickerFragmentInteractor.Actions.ShowStickers -> {
                    if (CommunicationBridge.isOrganizationMode.value==true){
                        exitSelectionMode()
                    }else {
                        commonStickerAdapter.updateList(action.stickers)
                        if (emojiCategoryListAdapter.isCategorySelected()) {
                            binding.rvStickers.visibility = View.VISIBLE
                            binding.btnSelect.visibility=View.GONE
                            binding.btnCancel.visibility=View.GONE
                            binding.rvStickers.adapter = commonStickerAdapter
                            currentRecyclerView = binding.rvStickers
                            commonStickerAdapter.isOpenedForCategory(true)
                            CommunicationBridge.isOrganizationMode.value = false
                            CommunicationBridge.isSelectionMode.value = false
                        }
                    }
                }

                is StickerFragmentInteractor.Actions.ShowDownloadedStickers -> {

                    if (CommunicationBridge.isOrganizationMode.value==true){
                        exitSelectionMode()
                    }else {
                        commonStickerAdapter.updateList(action.stickers)
                        commonStickerAdapter.isOpenedForCategory(false)
                        binding.btnSelect.visibility=View.GONE
                        binding.btnCancel.visibility=View.GONE
                        //commonStickerAdapter.isOpenedForOrganizeCategory(false)
                        binding.rvStickers.visibility = View.VISIBLE
                        binding.rvStickers.adapter = commonStickerAdapter
                        currentRecyclerView = binding.rvStickers
                    }

                }

                is StickerFragmentInteractor.Actions.ShowRecentStickers -> {
                    if (CommunicationBridge.isOrganizationMode.value==true){
                        exitSelectionMode()
                    }else {
                        commonStickerAdapter.updateList(action.stickers)
                        commonStickerAdapter.isOpenedForCategory(false)
                        binding.btnSelect.visibility=View.GONE
                        binding.btnCancel.visibility=View.GONE
                        //commonStickerAdapter.isOpenedForOrganizeCategory(false)
                        CommunicationBridge.isOrganizationMode.value = false
                        CommunicationBridge.isSelectionMode.value = false
                        binding.rvStickers.adapter = commonStickerAdapter
                        currentRecyclerView = binding.rvStickers
                    }
                }
                else -> {
                    exitSelectionMode()
                }
            }
        })

        CommunicationBridge.isSelectionMode.observe(viewLifecycleOwner, Observer {
        if (it){
            binding.btnSelect.text=resources.getString(R.string.move)
            }else{
            binding.btnSelect.text=resources.getString(R.string.select)
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
        setupEmojiRecyclerView()
        commonStickerAdapter = CommonStickerAdapter()
        favouritesAdapter = FavouritesAdapter()
        binding.rvStickers.adapter = commonStickerAdapter
        binding.rvStickers.visibility = View.VISIBLE
        currentRecyclerView = binding.rvStickers
        setupRecentSearchRecyclerView()
        commonStickerAdapter.onItemClick { sticker, position ->
            interactor.onStickerClick(sticker, position)
        }
        commonStickerAdapter.onItemLongClick { sticker, position ->
            interactor.onStickerLongClick(sticker, position)
        }

        commonStickerAdapter.setOnItemDoubleClickListener(object : OnItemDoubleClickListener {
            override fun onItemDoubleClick(sticker: Sticker, position: Int) {
                interactor.onStickerDoubleClick(sticker, position)
            }
        })

        commonStickerAdapter.onItemDelete { sticker, position ->
            interactor.onAddStickerToDeletedClick(sticker, true)
        }

        favouritesAdapter.onItemClick { sticker, position ->
            interactor.onFavStickerClick(sticker, position)
        }
        favouritesAdapter.onItemLongClick { sticker, position ->
            interactor.onFavStickerLongClick(sticker, position)
        }

        favouritesAdapter.setOnItemDoubleClickListener(object : OnItemDoubleClickListener {
            override fun onItemDoubleClick(sticker: Sticker, position: Int) {
                interactor.onFavStickerDoubleClick(sticker, position)
            }
        })
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

            btnSelect.setOnClickListener {
                //commonStickerAdapter.isOpenedForOrganizeCategory(true,true)
                if ( CommunicationBridge.isSelectionMode.value==false) {
                    CommunicationBridge.isSelectionMode.value = true
                    commonStickerAdapter.notifyDataSetChanged()
                }else {
                    moveToSelectedCategory()
                }
            }
            btnCancel.setOnClickListener {
                //commonStickerAdapter.isOpenedForOrganizeCategory(true,true)
                exitSelectionMode()
            }

            rvStickers.setOnClickListener {
                //commonStickerAdapter.isOpenedForOrganizeCategory(true,true)
                exitSelectionMode()
            }

            tilSearch.setOnClickListener {
                //commonStickerAdapter.isOpenedForOrganizeCategory(true,true)
                exitSelectionMode()
            }

            viewTopBg.setOnClickListener {
                //commonStickerAdapter.isOpenedForOrganizeCategory(true,true)
                exitSelectionMode()
            }

            imgScreenBg.setOnClickListener {
                //commonStickerAdapter.isOpenedForOrganizeCategory(true,true)
                exitSelectionMode()
            }
            imgAppIcon.setOnClickListener {
                //commonStickerAdapter.isOpenedForOrganizeCategory(true,true)
                exitSelectionMode()
            }
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
        exitSelectionMode()
    }

    private fun setupEmojiRecyclerView() {
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
                val dragFlags =  if (recyclerView.adapter is CommonStickerAdapter && (recyclerView.adapter as CommonStickerAdapter).didOpenForCategory) {
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
                commonStickerAdapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
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

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) { }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = requireActivity().currentFocus
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
        }
    }


    private fun exitSelectionMode(){
        CommunicationBridge.isOrganizationMode.value=false
        CommunicationBridge.isSelectionMode.value=false
        binding.btnSelect.visibility=View.GONE
        binding.btnCancel.visibility=View.GONE
        commonStickerAdapter.notifyDataSetChanged()
    }

    private fun moveToSelectedCategory(){
        Toast.makeText(requireContext(), "moved", Toast.LENGTH_SHORT).show()
        exitSelectionMode()
    }
}