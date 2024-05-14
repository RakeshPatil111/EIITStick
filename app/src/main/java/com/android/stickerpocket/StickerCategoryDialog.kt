package com.android.stickerpocket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import coil.load
import com.android.stickerpocket.databinding.CvStickerCaterogyDialogBinding


class StickerCategoryDialog : DialogFragment() {

    private var _binding: CvStickerCaterogyDialogBinding? = null
    val binding get() = _binding
    private var dialogX: Int = 0;
    private var dialogY: Int = 0;
    private var sticker: Sticker? = null
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
        listener: StickerCategoryDialogListener,
        dialogX: Int,
        dialogY: Int,
        sticker: Sticker
    ) {
        this.listener = listener
        this.dialogX = dialogX
        this.dialogY = dialogY
        this.sticker = sticker
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            sticker?.let {
                ivStickerThumbnail.load(it.thumbnail)
            }

            tvNewCategory.setOnClickListener {
                this@StickerCategoryDialog.dismiss()
                listener?.addNewCategory()
            }
        }
    }

    interface StickerCategoryDialogListener {
        fun addNewCategory()
        fun cancel()
    }

    override fun getTheme(): Int {
        return R.style.TransparentDialogTheme
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val window = dialog?.window
//        val params = window?.attributes
//        params?.x = dialogX
//        params?.y = dialogY - 700
//        println(" X: ${params?.x}, Y: ${params?.y}")
//        window?.attributes = params

        val param = window?.attributes
        param?.y = dialogY-700
        window?.attributes = param
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
}