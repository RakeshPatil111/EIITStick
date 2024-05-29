package com.android.stickerpocket.dtos

import com.android.stickerpocket.domain.model.Category

val defaultCategories = listOf(
    Category(
        id = 1,
        position = 0,
        name = "clapping hands",
        isDeleted = false,
        isHighlighted = true,
        unicode = "1F44F",
        html = "&#128079;"
    ),
    Category(
        id = 2,
        position = 1,
        name = "smiling face with heart-eyes",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1F60D",
        html = "&#128525;"
    ),
    Category(
        id = 3,
        position = 2,
        name = "Face with tears of joy",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1F602",
        html = "&#128514;"
    ),
    Category(
        id = 4,
        position = 3,
        name = "sleepy face",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1F62A",
        html = "&#128554;"
    ),
    Category(
        id = 5,
        position = 4,
        name = "sleeping face",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1F634",
        html = "&#128564;"
    ),
    Category(
        id = 6,
        position = 5,
        name = "face savoring food",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1F60B",
        html = "&#128523;"
    ),
    Category(
        id = 7,
        position = 6,
        name = "angry face",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1F620",
        html = "&#128544;"
    ),
    Category(
        id = 8,
        position = 7,
        name = "handshake",
        isDeleted = false,
        isHighlighted = false,
        unicode = "1F91D",
        html = "&#x1F91D;"
    )
)

fun getCategories() = defaultCategories