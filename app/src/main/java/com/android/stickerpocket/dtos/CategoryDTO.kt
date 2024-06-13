package com.android.stickerpocket.dtos

import com.android.stickerpocket.domain.model.Category

val defaultCategories = listOf(
    Category(
        id = 1,
        position = 0,
        name = "Surprise",
        isDeleted = false,
        isHighlighted = true,
        unicode = "1f632",
        html = "&#128562;"
    ),
    Category(
        id = 2,
        position = 1,
        name = "annoyed",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1f612",
        html = "&#128530;"
    ),
    Category(
        id = 3,
        position = 2,
        name = "Laugh",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1F602",
        html = "&#128514;"
    ),
    Category(
        id = 4,
        position = 3,
        name = "Love",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1F970",
        html = "&#x1F970;"
    ),
    Category(
        id = 5,
        position = 4,
        name = "Sad",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1f625",
        html = "&#128549;"
    )
)

fun getCategories() = defaultCategories