package com.android.stickerpocket.utils

import android.os.Parcelable
import androidx.annotation.StringRes
import com.android.stickerpocket.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tutorials (
    val tag: String,
    @StringRes val title: Int,
    val gif: Int?
): Parcelable

val sendStickerFromAppTut = Tutorials(
    AppConstants.SEND_STICKER_FROM_APP,
    R.string.send_sticker_from_app,
    R.raw.send_sticker_from_app
)

val sendStickerFromKeyboardTut = Tutorials(
    AppConstants.SEND_STICKER_FROM_KEYBOARD,
    R.string.send_sticker_from_keyboard,
    R.raw.send_sticker_from_keyboard
)

val openStickerPocketKeyboardTut = Tutorials(
    AppConstants.OPEN_STICKER_POCKET_KEYBOARD,
    R.string.open_sticker_pocket_keyboard,
    R.raw.open_sticker_pocket_keyboard
)

val setupStickerPocketKeyboardTut = Tutorials(
    AppConstants.SETUP_STICKER_POCKET_KEYBOARD,
    R.string.setup_sticker_pocket_keyboard,
    null
)

val downloadStickerTut = Tutorials(
    AppConstants.DOWNLOAD_STICKER,
    R.string.download_sticker,
    R.raw.download_sticker
)

val manageStickerTut = Tutorials(
    AppConstants.MANAGE_STICKERS,
    R.string.manage_sticker,
    R.raw.manage_stickers
)

val addTagToStickerTut = Tutorials(
    AppConstants.ADD_TAG_TO_STICKER,
    R.string.add_tags_to_sticker,
    null
)

val reorderStickerTut = Tutorials(
    AppConstants.REORDER_STICKERS,
    R.string.reorder_stickers,
    R.raw.reorder_stickers
)

val moveStickerToNewPocketTut = Tutorials(
    AppConstants.MOVE_STICKER_TO_NEW_POCKET,
    R.string.move_sticker_to_new_pocket,
    R.raw.move_sticker_to_new_pocket
)

val moveMultipleStickerToNewPocketTut = Tutorials(
    AppConstants.MOVE_MULTIPLE_STICKER_TO_NEW_POCKET,
    R.string.move_multiple_stickers_to_new_pocket,
    R.raw.move_multiple_sticker
)

val managePocketsTut = Tutorials(
    AppConstants.MANAGE_POCKETS,
    R.string.manage_pockets,
    R.raw.manage_pockets
)

val reorganizePocketTut = Tutorials(
    AppConstants.REORGANIZE_POCKET,
    R.string.reorganize_pockets,
    R.raw.reorgnize_pockets
)
/*
val welcomeMessageTut = Tutorials(
    AppConstants.WELCOME_MESSAGE,
    R.string.welcome_message,
    null
)

val stickerPocketLayoutTut = Tutorials(
    AppConstants.STICKER_POCKET_LAYOUT,
    R.string.sticker_pocket_layout,
    null
)

val importStickerFromWhatsappTut = Tutorials(
    AppConstants.IMPORT_STICKER_FROM_WHATSAPP,
    R.string.import_stickers_from_whatsapp,
    null
)

val importStickerFromPhotoTut = Tutorials(
    AppConstants.IMPORT_STICKER_FROM_PHOTO,
    R.string.import_stickers_from_photo,
    null
)*/


val tutorials = arrayListOf(
    sendStickerFromAppTut,
    sendStickerFromKeyboardTut,
    openStickerPocketKeyboardTut,
    setupStickerPocketKeyboardTut,
    downloadStickerTut,
    manageStickerTut,
    addTagToStickerTut,
    reorderStickerTut,
    moveStickerToNewPocketTut,
    moveMultipleStickerToNewPocketTut,
    managePocketsTut,
    reorganizePocketTut
)