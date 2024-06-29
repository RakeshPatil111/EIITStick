package com.android.stickerpocket

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
import com.android.stickerpocket.databinding.FragmentViewpagerBinding
import com.android.stickerpocket.utils.Tutorials

class ViewpagerFragment : Fragment() {

    private lateinit var binding: FragmentViewpagerBinding
    private lateinit var tutorial: Tutorials
    private var position: Int = 0
    private lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            tutorial = it.getParcelable(ARG_TUTORIAL)!!
            position = it.getInt(ARG_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewpagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            tutorial.let{
                val title = requireContext().getString(it.title)
                val pos = position + 1
                tvTutorialTitle.text = title
                tvTipTitle.text = "$pos - $title"
                sivGifImage.load(it.gif, imageLoader)
            }
        }
    }

    companion object{
        private const val ARG_TUTORIAL = "tutorial"
        private const val ARG_POSITION = "position"

        @JvmStatic
        fun newInstance(tutorial: Tutorials, position: Int) =
            ViewpagerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TUTORIAL, tutorial)
                    putInt(ARG_POSITION, position)
                }
            }
    }
}