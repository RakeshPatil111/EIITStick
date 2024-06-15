package com.android.stickerpocket.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.CvStickerDownloadDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StickerDownloadDialog : BottomSheetDialogFragment() {

    private var _binding: CvStickerDownloadDialogBinding? = null
    val binding get() = _binding
    private var listener: StickerDownloadDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = true
        _binding = CvStickerDownloadDialogBinding.inflate(
            inflater,
            container,
            false
        )

        return binding?.root
    }

    fun setupDialogInformation(
        listener: StickerDownloadDialogListener
    ) {
        this.listener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            tvDownloadSticker.setOnClickListener {
                this@StickerDownloadDialog.dismiss()
                listener?.onDownloadSticker()
            }
        }
    }

    interface StickerDownloadDialogListener {
        fun onDownloadSticker()
    }

    override fun getTheme(): Int {
        return R.style.TransparentDialogTheme
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}