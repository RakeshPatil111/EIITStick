package com.android.stickerpocket.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Favourites(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val mediaId: String?,
    val url: String?,
    val position: Int = 0
)