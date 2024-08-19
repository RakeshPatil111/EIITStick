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
import com.android.stickerpocket.databinding.FragmentSearchStickerBinding
import com.android.stickerpocket.presentation.dialog.StickerConfigDialog
import com.android.stickerpocket.presentation.dialog.StickerDownloadDialog
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.CommunicationBridge
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchStickerBinding.inflate(inflater, container, false)
        observeInteractor()


        if (CommunicationBridge.gifyEnabled.value==true){
            binding.rvGiphyStickerSection.visibility= View.VISIBLE
            binding.tvGiphyTitle.visibility= View.VISIBLE
            binding.tvGiphyTrendingTitle.visibility= View.VISIBLE

        }else{
            binding.rvGiphyStickerSection.visibility= View.GONE
            binding.tvGiphyTitle.visibility=  View.GONE
            binding.tvGiphyTrendingTitle.visibility=  View.GONE
        }

        if (CommunicationBridge.tenorEnabled.value==true){
            binding.rvTenorStickerSection.visibility= View.VISIBLE
            binding.tvTenorTitle.visibility= View.VISIBLE
            binding.tvTenorTrendingTitle.visibility= View.VISIBLE
        }else{
            binding.rvTenorStickerSection.visibility= View.GONE
            binding.tvTenorTitle.visibility= View.GONE
            binding.tvTenorTrendingTitle.visibility= View.GONE
        }


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
                    tvGiphyTrendingTitle.text = tietSearch.toString()
                    tvTenorTrendingTitle.text = tietSearch.toString()
                    //currentRecyclerView = rvStickers
                    //interactor.onEditTextClear()
                    //currentRecyclerView.visibility = View.VISIBLE
                } else {
                    tietSearch.text?.clear()
                    tvGiphyTrendingTitle.text = "Trending"
                    tvTenorTrendingTitle.text = "Trending"
                }
            }
        }

        return binding.root
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
                }else{
                    binding.llProgress.visibility = GONE
                }
                else -> {}
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interactor.initObserver(viewLifecycleOwner, viewModel)
        binding.apply {
            rvGiphyStickerSection.content = GPHContent.trendingGifs
            rvGiphyStickerSection.fixedSizeCells = true

            rvGiphyStickerSection.callback = object : GPHGridCallback {
                override fun contentDidUpdate(resultCount: Int) {}

                override fun didSelectMedia(media: Media) {
                    interactor.onStickerClick(media)
                }
            }

            ibtnConfigSticker.setOnClickListener {
                val rect = Rect()
                it.getGlobalVisibleRect(rect)
                val x = rect.width() / 2
                val y = rect.bottom - it.height
                StickerConfigDialog.newInstance(x, y).show(childFragmentManager, StickerConfigDialog.TAG)
            }
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
       CommunicationBridge.gifyEnabled.value=status
        if (status){
            binding.rvGiphyStickerSection.visibility= View.VISIBLE
            binding.tvGiphyTitle.visibility= View.VISIBLE
            binding.tvGiphyTrendingTitle.visibility= View.VISIBLE

        }else{
            binding.rvGiphyStickerSection.visibility= View.GONE
            binding.tvGiphyTitle.visibility=  View.GONE
            binding.tvGiphyTrendingTitle.visibility=  View.GONE
        }
    }

    override fun onTenorStatusChange(status: Boolean) {
        Log.d("on tenor switch change :"," $status")
        CommunicationBridge.tenorEnabled.value=status
        if (status){
            binding.rvTenorStickerSection.visibility= View.VISIBLE
            binding.tvTenorTitle.visibility= View.VISIBLE
            binding.tvTenorTrendingTitle.visibility= View.VISIBLE
        }else{
            binding.rvTenorStickerSection.visibility= View.GONE
            binding.tvTenorTitle.visibility= View.GONE
            binding.tvTenorTrendingTitle.visibility= View.GONE
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (binding.tietSearch.hasFocus()) {
            //interactor.onSearchClick()
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {}
}