package com.android.stickerpocket.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Emoji(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val category: String,
    val emoji: String,
    val html: String,
    val name: String,
    val order: String,
    val shortname: String,
    val unicode: String
)