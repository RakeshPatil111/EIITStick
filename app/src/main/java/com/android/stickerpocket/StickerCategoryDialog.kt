package com.android.stickerpocket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.stickerpocket.databinding.CvStickerCaterogyDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StickerCategoryDialog : BottomSheetDialogFragment() {

    private var _binding: CvStickerCaterogyDialogBinding? = null
    val binding get() = _binding
    private var listener: StickerCategoryDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = true
        _binding = CvStickerCaterogyDialogBinding.inflate(
            inflater,
            container,
            false
        )

        return binding?.root
    }

    fun setupDialogInformation(
        listener: StickerCategoryDialogListener
    ) {
        this.listener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            tvNewCategory.setOnClickListener {
                this@StickerCategoryDialog.dismiss()
                listener?.addNewCategory()
            }

            tvDeleteCategory.setOnClickListener {
                this@StickerCategoryDialog.dismiss()
                listener?.deleteCategory()
            }

            tvReorganize.setOnClickListener {
                this@StickerCategoryDialog.dismiss()
                listener?.reorganizeCategory()
            }
        }
    }

    interface StickerCategoryDialogListener {
        fun addNewCategory()
        fun reorganizeCategory()
        fun deleteCategory()
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