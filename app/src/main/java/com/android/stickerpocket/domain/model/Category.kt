package com.android.stickerpocket.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    val id: Int? = null,
    val name: String,
    @PrimaryKey
    val unicode: String,
    var position: Int,
    var isHighlighted: Boolean = false,
    var isDeleted: Boolean = false,
    var html: String = ""
)