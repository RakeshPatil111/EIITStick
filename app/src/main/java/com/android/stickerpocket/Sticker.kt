package com.android.stickerpocket

data class Sticker(
    val id: Int,
    val thumbnail: String,
    val title: String
)

val smiley = Sticker(
    1,
    "https://i.ibb.co/Bt6F31P/smile.png",
    "smiley"
)

val clap = Sticker(
    2,
    "https://i.ibb.co/dQDJM6b/clap.png",
    "clap"
)

val laugh = Sticker(
    3,
    "https://i.ibb.co/x8cn4Kc/laugh.png",
    "laugh"
)

val love = Sticker(
    4,
    "https://i.ibb.co/KVX2t7w/love.png",
    "love"
)

val unwell = Sticker(
    5,
    "https://i.ibb.co/VLjn4Hf/unwell.png",
    "Unwell"
)

val sleepy = Sticker(
    6,
    "https://i.ibb.co/5KG03VW/sleepy.png",
    "Sleepy"
)

val emoji = arrayListOf( clap, laugh, love, unwell, sleepy, clap, laugh, love, unwell, sleepy)