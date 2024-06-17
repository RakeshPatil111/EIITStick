package com.android.stickerpocket.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["stickerId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = Sticker::class,
            parentColumns = ["id"],
            childColumns = ["stickerId"],
            onDelete = ForeignKey.CASCADE)
    ])
data class RecentStickers(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val stickerId: Int
)