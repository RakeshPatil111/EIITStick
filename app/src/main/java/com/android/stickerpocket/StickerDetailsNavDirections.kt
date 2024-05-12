package com.android.stickerpocket

import android.os.Bundle
import androidx.navigation.NavDirections

data class StickerDetailsNavDirections(val sticker: Sticker): NavDirections {
    override val actionId: Int
        get() = R.id.action_stickerFragment_to_stickerDetailsFragment

    val bundle = Bundle().apply {
        putParcelable("sticker", sticker)
    }

    override val arguments: Bundle
        get() =  bundle
}
