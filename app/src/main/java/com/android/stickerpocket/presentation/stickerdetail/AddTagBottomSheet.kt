package com.android.stickerpocket.presentation.stickerdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.android.stickerpocket.databinding.LayoutAddTagBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTagBottomSheet: BottomSheetDialogFragment() {

    interface OnAddTagAction {
        fun onIgnoreClick()
        fun onAddClick(tag: String)
    }

    private lateinit var binding: LayoutAddTagBinding
    private var listener: OnAddTagAction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutAddTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btIgnore.setOnClickListener {
                listener?.onIgnoreClick()
                dismiss()
            }
            btAdd.setOnClickListener {
                if (etTag.text.isNullOrEmpty()) {
                    Toast.makeText(etTag.context, "Tag can not be empty", Toast.LENGTH_SHORT).show()
                } else {
                    listener?.onAddClick(etTag.text.toString())
                }
            }
        }
    }

    fun setListener(listener: OnAddTagAction) {
        this.listener = listener
    }
}