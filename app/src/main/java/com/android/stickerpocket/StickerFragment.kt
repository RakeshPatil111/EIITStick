package com.android.stickerpocket

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.emoji2.emojipicker.EmojiViewItem
import coil.load
import com.android.stickerpocket.databinding.FragmentStickerBinding
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.core.models.enums.MediaType
import com.giphy.sdk.ui.pagination.GPHContent
import com.giphy.sdk.ui.views.GPHGridCallback
import com.giphy.sdk.ui.views.GiphyGridView
import com.google.android.material.textview.MaterialTextView
import timber.log.Timber

class StickerFragment : Fragment(),
    StickerCategoryDialog.StickerCategoryDialogListener,
    EmojiPickerDialog.EmojiPickerDialogListener {

    private lateinit var binding: FragmentStickerBinding
    private lateinit var emojiCategoryListAdapter: EmojiCategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStickers.apply {
            showViewOnGiphy = false
            spanCount = 3
            direction = GiphyGridView.VERTICAL
            showCheckeredBackground = true
            fixedSizeCells = true
            cellPadding = 24
        }
        setupEmojiRecyclerView()
        binding.rvStickers.callback = object : GPHGridCallback {
            override fun contentDidUpdate(resultCount: Int) {
                Timber.d("contentDidUpdate $resultCount")
            }

            override fun didSelectMedia(media: Media) {
                Timber.d("didSelectMedia ${media.id}")
                StickerDialog.show(childFragmentManager, "https://i.ibb.co/353QnHz/first.gif")
            }
        }

        binding.rvStickers.content = GPHContent.recents
        binding.tietSearch.doAfterTextChanged {
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
                    if (it.toString().isNotEmpty()) {
                        binding.rvStickers.content = GPHContent.searchQuery(it.toString(), mediaType = MediaType.gif)
                    }
                }, 1500
            )
        }
    }

    private fun setupEmojiRecyclerView() {
        emojiCategoryListAdapter = EmojiCategoryListAdapter()
        binding.rvCategory.adapter = emojiCategoryListAdapter

        emojiCategoryListAdapter.stickerActionClick { sticker, _ ->
            binding.rvStickers.content = GPHContent.searchQuery(sticker.title.toString())
        }
        emojiCategoryListAdapter.stickerActionLongClick { sticker, position ->
            val view = binding.rvCategory[position].findViewById<View>(com.android.stickerpocket.R.id.cv_sticker)
            val location = IntArray(2)
            view?.getLocationInWindow(location)
            val x = location[0]
            val y = location[1]
            println("Item $position clicked, X: $x, Y: $y")

            binding.fadeUpView.visibility = View.VISIBLE
            val popupWindow = popupDisplay(sticker)
            popupWindow.showAsDropDown( view, 0, (-3.0 * (view.pivotY.toInt())).toInt())
            popupWindow.setOnDismissListener { binding.fadeUpView.visibility = View.GONE }

            /*val stickerCategoryDialog = StickerCategoryDialog()
            stickerCategoryDialog.setupDialogInformation(
                listener = this,
                dialogX = x,
                dialogY = y,
                sticker = sticker
            )
            stickerCategoryDialog.show(childFragmentManager, "StickerCategoryDialog")*/
        }

        emojiApiCallResponse()
    }
    private fun popupDisplay(sticker: Sticker): PopupWindow {
        val popupWindow = PopupWindow(requireContext())

        // inflate your layout or dynamically add view
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = inflater.inflate(R.layout.cv_sticker_caterogy_dialog, null)
        val imageView = view.rootView.findViewById<ImageView>(R.id.iv_sticker_thumbnail)
        val tvNewCategory = view.rootView.findViewById<MaterialTextView>(R.id.tv_new_category)

        sticker.let {
            imageView.load(it.thumbnail)
        }

        tvNewCategory.setOnClickListener {
            popupWindow.dismiss()
            binding.fadeUpView.visibility = View.GONE
            val emojiPickerDialog = EmojiPickerDialog()
            emojiPickerDialog.setDialogListener(
                listener = this
            )
            emojiPickerDialog.show(childFragmentManager, "EmojiPickerDialog")
        }

        popupWindow.isFocusable = true
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.contentView = view
        popupWindow.elevation = 12F
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return popupWindow
    }
    private fun emojiApiCallResponse() {
        emojiCategoryListAdapter.updateList(emoji)
    }

    private fun gifApiCallResponse() {
        //gifListAdapter.updateList(gifs, requireActivity(), "VERTICAL")
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                gifApiCallResponse()
            }, 500
        )
    }

    override fun addNewCategory() {
        val emojiPickerDialog = EmojiPickerDialog()
        emojiPickerDialog.setDialogListener(
            listener = this
        )
        emojiPickerDialog.show(childFragmentManager, "EmojiPickerDialog")
    }

    override fun addSelectedCategory(emojiItem: EmojiViewItem) {

    }


    override fun cancel() {
        Unit
    }
}