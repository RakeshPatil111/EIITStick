package com.android.stickerpocket.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.CvGifStickerBinding
import com.android.stickerpocket.utils.StickerExt.toFile
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StickerDialog : BottomSheetDialogFragment() {

    private var _binding: CvGifStickerBinding? = null
    val binding get() = _binding
   private lateinit var sticker: Sticker
    private lateinit var imageLoader: ImageLoader
    private var listener: StickerDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = true
        _binding = CvGifStickerBinding.inflate(
            inflater,
            container,
            false
        )

        imageLoader = ImageLoader
            .Builder(requireContext())
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            val file = sticker.toFile()
            val url = if (file.length() > 0) file else sticker.thumbnail
            sivGifImage.load(url, imageLoader) {
                target(
                    onStart = {
                        binding!!.loading.visibility = View.VISIBLE
                    },
                    onSuccess = {
                        binding!!.loading.visibility = View.GONE
                        sivGifImage.load(url, imageLoader)
                    },
                    onError = {
                        // Show error image
                        // Handle this scene
                        binding!!.loading.visibility = View.GONE
                    }
                )
            }

            tvInfo.setOnClickListener {
                this@StickerDialog.dismiss()
                listener?.onStickerInfoClick(sticker)
            }

            tvShare.setOnClickListener {
                this@StickerDialog.dismiss()
                listener?.onShareSticker(sticker)
            }
        }
    }

    companion object{
        private const val ARG_SELECTED_MEDIA_DETAILS = "arg_selected_media_details"
        private const val TAG = "SelectedMediaDialog"
    }

    interface StickerDialogListener {
        fun onStickerInfoClick(sticker: Sticker)
        fun onShareSticker(sticker: Sticker)
        fun onCancelClick()
    }

    override fun getTheme(): Int {
        return R.style.TransparentDialogTheme
    }

    fun setListener(listener: StickerDialogListener) {
        this.listener = listener
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun setSticker(sticker: Sticker) {
        this.sticker = sticker
    }
}