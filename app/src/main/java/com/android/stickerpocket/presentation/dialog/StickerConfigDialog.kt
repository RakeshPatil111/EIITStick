package com.android.stickerpocket.presentation.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.android.stickerpocket.R
import com.android.stickerpocket.databinding.CvStickerConfigDialogBinding

class StickerConfigDialog: DialogFragment() {

    private var _binding: CvStickerConfigDialogBinding? = null
    private val binding get() = _binding!!

    private var xPos: Int = 0
    private var yPos: Int = 0

    private var listener: StickerConfigDialogListener? = null

    interface StickerConfigDialogListener {
        fun onGiphyStatusChange(status: Boolean)
        fun onTenorStatusChange(status: Boolean)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
        setStyle(STYLE_NORMAL, R.style.TransparentDialogTheme)
        arguments?.let {
            xPos = it.getInt(ARG_X_POS)
            yPos = it.getInt(ARG_Y_POS)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            val params: WindowManager.LayoutParams = attributes
            params.gravity = Gravity.TOP or Gravity.END
            params.x = xPos
            params.y = yPos
            attributes = params
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as StickerConfigDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement StickerConfigDialogListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CvStickerConfigDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scGiphy.setOnCheckedChangeListener { _, isChecked ->
            listener?.onGiphyStatusChange(isChecked)
            //dismiss()
        }

        binding.scTenor.setOnCheckedChangeListener { _, isChecked ->
            listener?.onTenorStatusChange(isChecked)
            //dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CustomDialogFragment"
        private const val ARG_X_POS = "xPos"
        private const val ARG_Y_POS = "yPos"

        fun newInstance(x: Int, y: Int): StickerConfigDialog {
            val args = Bundle()
            args.putInt(ARG_X_POS, x)
            args.putInt(ARG_Y_POS, y)
            val fragment = StickerConfigDialog()
            fragment.arguments = args
            return fragment
        }
    }
}