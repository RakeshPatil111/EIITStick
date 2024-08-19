package com.android.stickerpocket.presentation.dialog

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
import com.android.stickerpocket.databinding.CvStickerDownloadDialogBinding
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.utils.StickerExt.stickerDTO
import com.android.stickerpocket.utils.StickerExt.toLoadableImage
import com.giphy.sdk.core.models.Media
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StickerDownloadDialog(
    private val media: StickerDTO
) : BottomSheetDialogFragment() {

    private var _binding: CvStickerDownloadDialogBinding? = null
    val binding get() = _binding
    private var listener: StickerDownloadDialogListener? = null
    private lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = true
        _binding = CvStickerDownloadDialogBinding.inflate(
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

    fun setupDialogInformation(
        listener: StickerDownloadDialogListener
    ) {
        this.listener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            sivGifImage.load(media.toLoadableImage(), imageLoader) {
                target(
                    onStart = {
                        binding!!.loading.visibility = View.VISIBLE
                    },
                    onSuccess = {
                        binding!!.loading.visibility = View.GONE
                        sivGifImage.load(media.toLoadableImage(), imageLoader)
                    },
                    onError = {
                        // Show error image
                        // Handle this scene
                        binding!!.loading.visibility = View.GONE
                    }
                )
            }

            tvDownloadSticker.setOnClickListener {
                this@StickerDownloadDialog.dismiss()
                listener?.onDownloadSticker()
            }
        }
    }

    interface StickerDownloadDialogListener {
        fun onDownloadSticker()
    }

    override fun getTheme(): Int {
        return R.style.TransparentDialogTheme
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}