package com.android.stickerpocket.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentSearch(
    @PrimaryKey
    val query: String,
    var time: Long
)
