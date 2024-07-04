package com.android.stickerpocket.presentation.stickerdetail

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.FragmentStickerDetailsBinding
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.presentation.sticker.StickerDetailFragmentInteractor
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.StickerExt.toLoadableImage

class StickerDetailsFragment : Fragment() {

    private lateinit var binding: FragmentStickerDetailsBinding
    private lateinit var imageLoader: ImageLoader
    private var stickerDTO: StickerDTO? = null
    private val viewModel by activityViewModels<StickerViewModel>()
    private val interactor by lazy {
        StickerDetailFragmentInteractor()
    }

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
        initObserver()
        stickerDTO = arguments?.getParcelable("sticker")
        interactor.onCreate(stickerDTO)
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            ibFav.setOnClickListener {
                interactor.onAddToFavClick()
            }
            ibtnShare.setOnClickListener {
                interactor.onShareClick()
            }
            btAddTag.setOnClickListener {
                interactor.onAddTagClick()
            }
        }
    }

    private fun initObserver() {
        interactor.initObserver(viewLifecycleOwner, viewModel)
        interactor.liveData.observe(viewLifecycleOwner) { it ->
            when (val action = it.getContentIfNotHandled()) {
                is StickerDetailFragmentInteractor.Actions.ShowStickerDetails -> {
                    binding.apply {
                        sivGifImage.load(action.stickerDTO.toLoadableImage(), imageLoader) {
                            target(
                                onSuccess = {
                                    sivGifImage.load(action.stickerDTO.toLoadableImage(), imageLoader)
                                }
                            )
                        }
                        tvId.text =
                            "${if (action.stickerDTO.id < 0) action.stickerDTO.id * -1 else action.stickerDTO.id}"
                        action.stickerDTO.source?.let { s ->
                            if (s.isNotEmpty())
                                tvSource.text = s
                        }
                        action.stickerDTO.creator?.let { s ->
                            tvCreator.text = s
                        }
                        action.stickerDTO.tags?.let { s ->
                            if (s.isNotEmpty()) {
                                tvTagList.text = s.joinToString()
                            } else {
                                tvTagList.visibility = View.GONE
                            }
                        }
                    }
                }

                is StickerDetailFragmentInteractor.Actions.ShowFilledFavIcon -> {
                    binding.ibFav.setImageResource(R.drawable.ic_fav_filled)
                }

                is StickerDetailFragmentInteractor.Actions.ShowOutlinedFavIcon -> {
                    binding.ibFav.apply {
                       setImageResource(R.drawable.ic_favorite_border)
                        setColorFilter(ContextCompat.getColor(this.context, R.color.color_primary))
                    }
                }

                is StickerDetailFragmentInteractor.Actions.ShowAddTagBottomSheet -> {
                    val addTagBottomSheet = AddTagBottomSheet()
                    addTagBottomSheet.setListener(object : AddTagBottomSheet.OnAddTagAction {
                        override fun onIgnoreClick() {
                            addTagBottomSheet.dismiss()
                        }

                        override fun onAddClick(tag: String) {
                            addTagBottomSheet.dismiss()
                            interactor.onAddTag(tag, stickerDTO!!.stickerId)
                        }

                    })
                    addTagBottomSheet.show(parentFragmentManager, "AddTagBottomSheet")
                }
                else -> {}
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interactor.onViewCreated()
    }
}