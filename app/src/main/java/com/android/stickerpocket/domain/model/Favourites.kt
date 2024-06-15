package com.android.stickerpocket.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
class Favourites(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val mediaId: String?,
    val url: String?,
    val position: Int = 0,
    val name: String,
    val date: Long = Date().time,
    val stickerId: String
)