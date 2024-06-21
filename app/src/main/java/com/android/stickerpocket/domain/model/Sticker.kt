package com.android.stickerpocket.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sticker(
    val name: String,
    var categoryId: Int? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var isDeleted: Boolean = false,
    var url: String,
    var isDownloaded: Boolean = true,
    var isFavourite: Boolean = false,
    var localPath: String = "",
    var tags: String? = null,
    var creator: String? = null,
    var source: String? = null,
    var position: Int = 0,
    val mediaId: String = "", // GIPHY mediaId
    var isOrganizeMode: Boolean = false
)