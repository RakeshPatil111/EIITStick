package com.android.stickerpocket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.fragment.app.DialogFragment
import com.android.stickerpocket.databinding.CvEmojiPickerDialogBinding

class EmojiPickerDialog: DialogFragment() {

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
                val title = emojiItem.emoji[1].toString()
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