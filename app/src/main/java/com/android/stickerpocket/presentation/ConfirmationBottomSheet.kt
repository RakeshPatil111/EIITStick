package com.android.stickerpocket.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.stickerpocket.databinding.CustomConfirmationLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmationBottomSheet: BottomSheetDialogFragment() {
    private lateinit var message: String
    private lateinit var negativeOption: String
    private lateinit var positiveOption: String
    private lateinit var binding: CustomConfirmationLayoutBinding
    private var listner: OnConfirmationClickListner? = null

    companion object {

        fun create(message: String, negativeOption: String, positiveOption: String): ConfirmationBottomSheet {
            val fragment = ConfirmationBottomSheet()
            fragment.message = message
            fragment.negativeOption = negativeOption
            fragment.positiveOption = positiveOption
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CustomConfirmationLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvMessage.text = message
            tvNegative.text = negativeOption
            tvPositive.text = positiveOption

            tvNegative.setOnClickListener {
                dismiss()
            }

            tvPositive.setOnClickListener {
                listner?.onConfirm()
                dismiss()
            }
        }
    }

    fun setClickListener(listener: OnConfirmationClickListner) {
        this.listner = listener
    }
    interface OnConfirmationClickListner {
        fun onConfirm()
    }
}