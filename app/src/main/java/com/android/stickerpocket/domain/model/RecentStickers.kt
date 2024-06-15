package com.android.stickerpocket.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentStickers(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val stickerId: Int
)