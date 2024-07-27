package com.android.stickerpocket.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.emoji2.emojipicker.EmojiViewItem
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.CvEmojiPickerDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EmojiPickerDialog: BottomSheetDialogFragment() {

    private var _binding: CvEmojiPickerDialogBinding? = null
    val binding get()= _binding
    private var listener: EmojiPickerDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = true
        _binding = CvEmojiPickerDialogBinding.inflate(inflater, container, false)

        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            emojiPickerView.setOnEmojiPickedListener{ emojiItem ->
                this@EmojiPickerDialog.dismiss()
                listener?.addSelectedCategory(emojiItem)
            }
        }
    }

    fun setDialogListener(
        listener: EmojiPickerDialogListener
    ){
        this.listener = listener
    }

    interface EmojiPickerDialogListener {
        fun addSelectedCategory(emojiItem: EmojiViewItem)
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