package com.android.stickerpocket.presentation.settings.delete

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.stickerpocket.databinding.FragmentDeletedStickerBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.presentation.CommonStickerAdapter
import com.android.stickerpocket.presentation.ConfirmationBottomSheet
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.CommunicationBridge

class DeletedStickerFragment : Fragment() {

    private lateinit var binding: FragmentDeletedStickerBinding
    private val viewModel: StickerViewModel by viewModels<StickerViewModel>()
    private val interactor by lazy {
        DeleteFragmentInterActor()
    }
    private val adapter = DeletedStickerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        interactor.initObserver(viewLifecycleOwner, viewModel)
        interactor.liveData.observe(viewLifecycleOwner, Observer {
            when (val action = it.getContentIfNotHandled()) {
                is DeleteFragmentInterActor.Actions.ShowStickers -> {
                    binding.apply {
                        rvStickers.adapter = adapter
                        adapter.updateList(action.stickers)
                    }
                }

                is DeleteFragmentInterActor.Actions.ShowProgress -> {
                    binding.llProgress.visibility = View.VISIBLE
                }

                is DeleteFragmentInterActor.Actions.HideProgress -> {
                    binding.llProgress.visibility = View.GONE
                }

                is DeleteFragmentInterActor.Actions.HideRecyclerView -> {
                    binding.llProgress.visibility = View.GONE
                    binding.rvStickers.visibility = View.GONE
                    binding.btnDeleteAll.visibility = View.GONE
                    binding.btnSelect.visibility = View.GONE
                }

                else -> {}
            }
        })
        binding = FragmentDeletedStickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        interactor.onViewCreated()

        binding.apply {
            btnSelect.setOnClickListener {
                if (adapter.getSelectedStickers().isEmpty()) {
                    Toast.makeText(requireContext(), "No stickers selected", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                interactor.onRestoreStickers(adapter.getSelectedStickers())
            }
            tvBack.setOnClickListener { findNavController().popBackStack() }
            btnDeleteAll.setOnClickListener {
                if (adapter.getSelectedStickers().isEmpty()) {
                    val bottomSheet = ConfirmationBottomSheet.create("Are you sure you want to delete all stickers?",
                        negativeOption = "No", positiveOption = "Yes")
                    bottomSheet.show(parentFragmentManager, "ConfirmationBottomSheet")

                    bottomSheet.setClickListener(object : ConfirmationBottomSheet.OnConfirmationClickListner {
                        override fun onConfirm() {
                            interactor.onDeleteAll()
                        }

                    })
                }
            }
        }

        adapter.setOnClickListener(object : DeletedStickerAdapter.OnDeleteStickerClick {
            override fun onClick(sticker: Sticker) {

                val bottomSheet = ConfirmationBottomSheet.create("Are you sure you want to delete this stickers?",
                    negativeOption = "No", positiveOption = "Yes")
                bottomSheet.show(parentFragmentManager, "ConfirmationBottomSheet")

                bottomSheet.setClickListener(object : ConfirmationBottomSheet.OnConfirmationClickListner {
                    override fun onConfirm() {
                        interactor.onDelete(sticker)
                    }

                })
            }

            override fun onStickerCheck() {
                if (adapter.getSelectedStickers().isEmpty()) {
                    binding.btnSelect.visibility = View.GONE
                } else {
                    binding.btnSelect.visibility = View.VISIBLE
                }
            }

        })
    }
}