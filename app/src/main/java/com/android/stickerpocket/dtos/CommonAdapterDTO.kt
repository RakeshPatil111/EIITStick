package com.android.stickerpocket.dtos

data class CommonAdapterDTO (
    val isSticker: Boolean,
    val name: String,
    var categoryId: Int? = null,
    val id: Int? = null,
    var url: String = "",
    var isDownloaded: Boolean = true,
    var isFavourite: Boolean = false,
    var localPath: String = "",
    var tags: String? = null,
    var creator: String? = null,
    var source: String? = null,
    val mediaId: String = "",
    val unicode: String = "",
    var position: Int = -1,
    var isHighlighted: Boolean = false,
    var isDeleted: Boolean = false,
    var html: String = ""
)