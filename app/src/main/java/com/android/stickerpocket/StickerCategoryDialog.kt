package com.android.stickerpocket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import coil.load
import com.android.stickerpocket.databinding.CvStickerCaterogyDialogBinding


class StickerCategoryDialog : DialogFragment() {

    private var _binding: CvStickerCaterogyDialogBinding? = null
    val binding get() = _binding
    private var dialogX: Int = 0;
    private var dialogY: Int = 0;
    private var sticker: Sticker? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments

        dialogX =
           if (arguments?.containsKey(ARG_DIALOG_X) == true) {
               arguments.getInt(ARG_DIALOG_X)
           } else {
               Log.d("Position", "x value not passed in")
               0
           }

        dialogY =
            if (arguments?.containsKey(ARG_DIALOG_Y) == true) {
                arguments.getInt(ARG_DIALOG_Y)
            } else {
                Log.d("Position", "y value not passed in")
                0
            }

        sticker =
            if (arguments?.containsKey(ARG_STICKER) == true) {
                arguments.getParcelable(ARG_STICKER)
            } else{
                Log.d("Sticker", "sticker object not passed in")
                null
            }

        binding?.apply {
            sticker?.let {
                ivStickerThumbnail.load(it.thumbnail)
            }
        }
    }

    companion object{
        private const val TAG = "SelectedMediaDialog"
        private const val ARG_DIALOG_X = "arg_dialog_x"
        private const val ARG_DIALOG_Y = "arg_dialog_y"
        private const val ARG_STICKER = "arg_sticker"

        fun show(
            fragmentManager: FragmentManager,
            sticker: Sticker,
            dialogX: Int,
            dialogY: Int
        ) {
            val stickerCategoryDialog = StickerCategoryDialog()
            stickerCategoryDialog.arguments = Bundle().apply {
                putInt(ARG_DIALOG_X, dialogX)
                putInt(ARG_DIALOG_Y, dialogY)
                putParcelable(ARG_STICKER, sticker)
            }
            stickerCategoryDialog.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as StickerCategoryDialog?)?.dismiss()
        }
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
        val args = requireArguments()
        val dialogX = args.getInt(ARG_DIALOG_X)
        val dialogY = args.getInt(ARG_DIALOG_Y)

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