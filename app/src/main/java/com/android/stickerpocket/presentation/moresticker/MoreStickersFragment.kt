package com.android.stickerpocket.presentation.moresticker

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.stickerpocket.databinding.FragmentSearchStickerBinding
import com.android.stickerpocket.presentation.dialog.StickerConfigDialog
import com.android.stickerpocket.presentation.dialog.StickerDownloadDialog
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.StickerExt.stickerDTO
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback

class MoreStickersFragment : Fragment(), StickerConfigDialog.StickerConfigDialogListener {

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

    override fun onGiphyStatusChange(status: Boolean) {
        Log.d("on giphy switch change :"," $status")
    }

    override fun onTenorStatusChange(status: Boolean) {
        Log.d("on tenor switch change :"," $status")
    }
}