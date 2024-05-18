package com.android.stickerpocket.presentation

data class Gifs(
    val parentId: Int,
    val id: Int,
    val thumbnail: String,
    val title: String
)

val first1 = Gifs(
    1,
    1,
    "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExemd3M29wajFkeDBjd2F6aGczcGY2YWJtNjJ6cmVxYnV1dDdpZzNoNCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/IRFQYGCokErS0/giphy.webp",
    "test1"
)

val first2 = Gifs(
    1,
    1,
    "https://i.ibb.co/353QnHz/first.gif",
    "test2"
)

val second1 = Gifs(
    2,
    1,
    "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExemd3M29wajFkeDBjd2F6aGczcGY2YWJtNjJ6cmVxYnV1dDdpZzNoNCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/IRFQYGCokErS0/giphy.webp",
    "test1"
)

val second2 = Gifs(
    2,
    1,
    "https://i.ibb.co/353QnHz/first.gif",
    "test2"
)

val third1 = Gifs(
    3,
    1,
    "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExemd3M29wajFkeDBjd2F6aGczcGY2YWJtNjJ6cmVxYnV1dDdpZzNoNCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/IRFQYGCokErS0/giphy.webp",
    "test1"
)

val third2 = Gifs(
    3,
    1,
    "https://i.ibb.co/353QnHz/first.gif",
    "test2"
)

val fourth1 = Gifs(
    4,
    1,
    "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExemd3M29wajFkeDBjd2F6aGczcGY2YWJtNjJ6cmVxYnV1dDdpZzNoNCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/IRFQYGCokErS0/giphy.webp",
    "test1"
)

val fourth2 = Gifs(
    4,
    1,
    "https://i.ibb.co/353QnHz/first.gif",
    "test2"
)

val gifs = arrayListOf(first2, first1, second1, second2, third1, third2, fourth1, fourth2, first2, first1, second1, second2, third1, third2, fourth1, fourth2, first2, first1, second1, second2, third1, third2, fourth1, fourth2, first2, first1, second1, second2, third1, third2, fourth1, fourth2)