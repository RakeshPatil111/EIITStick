package com.android.stickerpocket.presentation

import android.os.Bundle
import androidx.navigation.NavDirections
import com.android.stickerpocket.R

data class StickerDetailsNavDirections(val stickerDTO: StickerDTO): NavDirections {
    override val actionId: Int
        get() = R.id.action_stickerFragment_to_stickerDetailsFragment

    val bundle = Bundle().apply {
        putParcelable("sticker", stickerDTO)
    }

    override val arguments: Bundle
        get() =  bundle
}
