package com.android.stickerpocket.presentation.settings

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
import com.android.stickerpocket.databinding.FragmentTutorialDetailsBinding

class TutorialDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTutorialDetailsBinding
    private val args: TutorialDetailsFragmentArgs by navArgs()
    private lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tutorial = args.selectedTut
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
        binding.apply{
            tvTutorialTitle.text = requireContext().getString(tutorial.title)
            sivGifImage.load(tutorial.gif, imageLoader)
        }
    }
}