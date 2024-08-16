package com.android.stickerpocket.presentation.moresticker

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.databinding.FragmentSearchStickerBinding
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.presentation.dialog.StickerConfigDialog
import com.android.stickerpocket.presentation.dialog.StickerDownloadDialog
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.StickerExt.stickerDTO
import com.google.android.material.textfield.TextInputEditText

class MoreStickersFragment : Fragment(),
    StickerConfigDialog.StickerConfigDialogListener,
    TextWatcher {

    private lateinit var binding: FragmentSearchStickerBinding
    private val viewModel by viewModels<StickerViewModel>()

    private val interactor by lazy {
        MoreStickerFragmentInteractor()
    }

    private lateinit var trendingGifAdapter: TrendingTenorAdapter
    private lateinit var trendingTenorAdapter: TrendingTenorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchStickerBinding.inflate(inflater, container, false)
        interactor.initObserver(viewLifecycleOwner, viewModel)
        observeInteractor()

        binding.apply {
            tietSearch.setOnEditorActionListener { view, actionId, event ->
                if (actionId == IME_ACTION_SEARCH ||
                    (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                ) {
                    val query = view.text.toString()
                    hideKeyboard()
                    if (query.isEmpty() && tietSearch.hasFocus()) {
                        rvRecentSearch.visibility = View.VISIBLE
                        rvGiphyStickerSection.visibility = View.GONE
                        tvGiphyTrendingTitle.visibility = View.GONE
                        tvGiphyTitle.visibility = View.GONE

                    } else {

                    }
                    true
                } else {
                    false
                }
            }

            tilSearch.setEndIconOnClickListener {
                if (tietSearch.text.isNullOrEmpty()) {
                    hideKeyboard()
                    removeChangeListeners(tietSearch)
                    tietSearch.clearFocus()
                    tietSearch.text?.clear()
                    addChangeListeners(tietSearch)
                    rvRecentSearch.visibility = View.GONE
                    //currentRecyclerView = rvStickers
                    //interactor.onEditTextClear()
                    //currentRecyclerView.visibility = View.VISIBLE
                } else {
                    tietSearch.text?.clear()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
//            rvGiphyStickerSection.content = GPHContent.trendingGifs
//            rvGiphyStickerSection.fixedSizeCells = true
//
//            rvGiphyStickerSection.callback = object : GPHGridCallback {
//                override fun contentDidUpdate(resultCount: Int) {}
//
//                override fun didSelectMedia(media: Media) {
//                    interactor.onStickerClick(media)
//                }
//            }

            ibtnConfigSticker.setOnClickListener {
                val rect = Rect()
                it.getGlobalVisibleRect(rect)
                val x = rect.width() / 2
                val y = rect.bottom - it.height
                StickerConfigDialog.newInstance(x, y).show(childFragmentManager, StickerConfigDialog.TAG)
            }
        }
        interactor.onViewCreated()
    }

    private fun observeInteractor() {
        interactor.liveData.observe(viewLifecycleOwner, Observer {
            when (val action = it.getContentIfNotHandled()) {
                is MoreStickerFragmentInteractor.Actions.ShowDownloadStickerDialog -> {

                    val stickerDownloadDialog = StickerDownloadDialog(action.media)
                    stickerDownloadDialog.setupDialogInformation(
                        object : StickerDownloadDialog.StickerDownloadDialogListener {
                            override fun onDownloadSticker() {
                                binding.llProgress.visibility = VISIBLE
                                Handler(Looper.getMainLooper()).postDelayed({
                                        binding.llProgress.visibility = GONE
                                    }, 4000
                                )
                                val sticker = action.media
                                viewModel.saveSingleSticker(sticker)
                            }
                        }
                    )
                    stickerDownloadDialog.show(
                        childFragmentManager,
                        "StickerDownloadDialog"
                    )
                }
                is MoreStickerFragmentInteractor.Actions.ShowProgress -> if(action.showProgress){
                    binding.llProgress.visibility = VISIBLE
                } else {
                    binding.llProgress.visibility = GONE
                }

                is MoreStickerFragmentInteractor.Actions.ShowTrendingGiphyStickers -> {
                    binding.apply {
                        if (!::trendingGifAdapter.isInitialized && !::trendingTenorAdapter.isInitialized ) {
                            initGiphyAdapter()
                            initTenorAdapter()
                        }
                        trendingGifAdapter.updateList(action.giphyGifs)
                        trendingTenorAdapter.updateList(action.tenorGifs)
                    }
                }

                else -> {}
            }
        })
    }

    private fun initTenorAdapter() {
        trendingTenorAdapter = TrendingTenorAdapter()
        binding.apply {
            rvTenorStickerSection.adapter = trendingTenorAdapter
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(rvTenorStickerSection)
            rvTenorStickerSection.layoutManager =
                GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)
            trendingTenorAdapter.setListener(object : TrendingTenorAdapter.OnTrendingGifListener {
                override fun onGifItemClick(item: StickerDTO) {
                    interactor.onStickerClick(item)
                }

                override fun loadMore() {
                    //interactor.onLoadMoreTrendingStickers()
                }

            })
        }
    }

    private fun initGiphyAdapter() {
        trendingGifAdapter = TrendingTenorAdapter()
        binding.apply {
            rvGiphyStickerSection.adapter = trendingGifAdapter
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(rvGiphyStickerSection)
            rvGiphyStickerSection.layoutManager =
                GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)
            trendingGifAdapter.setListener(object : TrendingTenorAdapter.OnTrendingGifListener {
                override fun onGifItemClick(item: StickerDTO) {
                    interactor.onStickerClick(item)
                }

                override fun loadMore() {
                    //interactor.onLoadMoreTrendingStickers()
                }

            })
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = requireActivity().currentFocus
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, 0)
        }
    }

    private fun addChangeListeners(tietSearch: TextInputEditText) {
        tietSearch.addTextChangedListener(this)
        tietSearch.setOnFocusChangeListener { v, hasFocus ->
            tietSearch.isCursorVisible = true
            if (hasFocus) {
                //interactor.onSearchClick()
            } else {
                tietSearch.isCursorVisible = false
            }
        }
    }

    private fun removeChangeListeners(tietSearch: TextInputEditText) {
        tietSearch.removeTextChangedListener(this)
        tietSearch.setOnClickListener(null)
        //exitSelectionMode()
    }

    override fun onGiphyStatusChange(status: Boolean) {
        Log.d("on giphy switch change :"," $status")
    }

    override fun onTenorStatusChange(status: Boolean) {
        Log.d("on tenor switch change :"," $status")
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (binding.tietSearch.hasFocus()) {
            //interactor.onSearchClick()
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {}

    override fun onDestroyView() {
        super.onDestroyView()
        interactor.onDestroy()
    }
}