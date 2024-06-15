package com.android.stickerpocket.presentation

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.databinding.FragmentStickerDetailsBinding
import com.android.stickerpocket.utils.StickerExt.toFile

class StickerDetailsFragment : Fragment() {

    private lateinit var binding: FragmentStickerDetailsBinding
    private lateinit var imageLoader: ImageLoader
    private var stickerDTO: StickerDTO? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStickerDetailsBinding.inflate(inflater, container, false)

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
        stickerDTO = arguments?.getParcelable("sticker")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            stickerDTO?.let {
                val file = it.toFile()
                val url = if (file.length() > 0) file else it.thumbnail
                sivGifImage.load(url, imageLoader) {
                    target(
                        onSuccess = {
                            sivGifImage.load(url, imageLoader)
                        }
                    )
                }
                tvId.text = "${it.id}"
                it.source?.let { s ->
                    tvSource.text = s
                }
                it.creator?.let { s ->
                    tvCreator.text = s
                }
                it.tags?.let { s ->
                    tvTagList.text = s.joinToString { "," }
                }
            }
        }
    }
}