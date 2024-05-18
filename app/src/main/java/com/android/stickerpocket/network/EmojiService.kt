package com.android.stickerpocket.network

import com.android.stickerpocket.network.response.Emojis
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL =
    "https://gist.github.com/oliveratgithub/0bf11a9aff0d6da7b46f1490f86a71eb/#file-emojis-json"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .build()
interface EmojiService {
    @GET
    suspend fun getEmoji() : List<Emojis>
}

object MarsApi {
    val retrofitService: EmojiService by lazy {
        retrofit.create(EmojiService::class.java)
    }
}