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
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.stickerpocket.databinding.FragmentSearchStickerBinding
import com.android.stickerpocket.network.response.Data
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.presentation.dialog.StickerConfigDialog
import com.android.stickerpocket.presentation.dialog.StickerDownloadDialog
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.StickerExt.stickerDTO
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MoreStickersFragment : Fragment(),
    StickerConfigDialog.StickerConfigDialogListener,
    TextWatcher {

    private lateinit var binding: FragmentSearchStickerBinding
    private val viewModel by viewModels<StickerViewModel>()

    private val interactor by lazy {
        MoreStickerFragmentInteractor()
    }

    private lateinit var trendingGifAdapter: TrendingGiphyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchStickerBinding.inflate(inflater, container, false)
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

    private fun observeInteractor() {
        interactor.liveData.observe(viewLifecycleOwner, Observer {
            when (val action = it.getContentIfNotHandled()) {
                is MoreStickerFragmentInteractor.Actions.ShowDownloadStickerDialog -> {

                    val stickerDownloadDialog = StickerDownloadDialog()
                    stickerDownloadDialog.setupDialogInformation(
                        object : StickerDownloadDialog.StickerDownloadDialogListener {
                            override fun onDownloadSticker() {
                                binding.llProgress.visibility = VISIBLE
                                Handler(Looper.getMainLooper()).postDelayed({
                                        binding.llProgress.visibility = GONE
                                    }, 4000
                                )
                                val sticker = action.media.stickerDTO()
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
                        if (!::trendingGifAdapter.isInitialized) {
                            trendingGifAdapter = TrendingGiphyAdapter()
                            rvGiphyStickerSection.adapter = trendingGifAdapter
                            val snapHelper = PagerSnapHelper()
                            snapHelper.attachToRecyclerView(rvGiphyStickerSection)
                            rvGiphyStickerSection.layoutManager =
                                GridLayoutManager(requireContext(), 2, RecyclerView.HORIZONTAL, false)
                            trendingGifAdapter.setListener(object : TrendingGiphyAdapter.OnTrendingGifListener {
                                override fun onGifItemClick(item: StickerDTO) {

                                }

                                override fun loadMore() {
                                    interactor.onLoadMoreTrendingStickers()
                                }

                            })
                        }
                        trendingGifAdapter.updateList(action.data, action.page)
                        //rvGiphyStickerSection.scrollToPosition((action.page - 1) * 25)
                    }
                }
                else -> {}
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interactor.initObserver(viewLifecycleOwner, viewModel)
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