package com.android.stickerpocket

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.databinding.FragmentStickerDetailsBinding

class StickerDetailsFragment : Fragment() {

    private lateinit var binding: FragmentStickerDetailsBinding
    private lateinit var imageLoader: ImageLoader

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            sivGifImage.load("https://i.ibb.co/353QnHz/first.gif", imageLoader){
                target(
                    onSuccess = {
                        sivGifImage.load("https://i.ibb.co/353QnHz/first.gif", imageLoader)
                    }
                )
            }
        }
    }
}