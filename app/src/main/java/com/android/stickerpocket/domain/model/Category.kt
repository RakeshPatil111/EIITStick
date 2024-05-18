package com.android.stickerpocket.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val unicode: String,
    val position: Int,
    val isHighlighted: Boolean = false,
    val isDeleted: Boolean = false
)