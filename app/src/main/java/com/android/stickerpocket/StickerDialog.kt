package com.android.stickerpocket

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.databinding.CvGifStickerBinding
import com.giphy.sdk.core.models.Media

class StickerDialog : DialogFragment() {

    private var _binding: CvGifStickerBinding? = null
    val binding get() = _binding
   /* private var selectedMedia: Media? = null*/
    private var gif: String? = null
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as StickerDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() + "must implement StickerDialogListener"
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments
        /*selectedMedia =
            if (arguments?.containsKey(ARG_SELECTED_MEDIA_DETAILS) == true) {
                arguments.getString(ARG_SELECTED_MEDIA_DETAILS)
            } else {
                Log.d("Media", "media is not passed in")
                null
            }*/
        gif =
            if (arguments?.containsKey(ARG_SELECTED_MEDIA_DETAILS) == true) {
                arguments.getString(ARG_SELECTED_MEDIA_DETAILS)
            } else {
                Log.d("Media", "media is not passed in")
                null
            }


        binding?.apply {
            /*selectedMedia?.let {media ->
                sivGifImage.load(media.images.preview, imageLoader){
                    target(
                        onSuccess = {
                            sivGifImage.load(media.images.preview, imageLoader)
                        }
                    )
                }*/

            sivGifImage.load(gif, imageLoader){
                target(
                    onSuccess = {
                        sivGifImage.load(gif, imageLoader)
                    }
                )
            }

            tvInfo.setOnClickListener {
                this@StickerDialog.dismiss()
                listener?.selectedSticker(Sticker(1,gif,"new one"))
            }
        }
    }

    companion object{
        private const val ARG_SELECTED_MEDIA_DETAILS = "arg_selected_media_details"
        private const val TAG = "SelectedMediaDialog"

        fun show(
            fragmentManager: FragmentManager,
            gifData: String
            /*selectedMediaDetails: Media*/
        ) {
            val stickerDialog = StickerDialog()
            stickerDialog.arguments = Bundle().apply {
                putString(ARG_SELECTED_MEDIA_DETAILS, gifData)
            }
            stickerDialog.show(fragmentManager, TAG)
        }


        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as StickerDialog?)?.dismiss()
        }
    }

    interface StickerDialogListener {
        fun selectedSticker(sticker: Sticker)
        fun cancel()
    }

    override fun getTheme(): Int {
        return R.style.TransparentDialogTheme
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}