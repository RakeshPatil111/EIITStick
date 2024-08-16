package com.android.stickerpocket.network

import com.android.stickerpocket.network.response.GifResponse
import retrofit2.Response

object GifServiceImpl {
    suspend fun getGifs(limit: Int = 25, query: String = "", page: Int = 0, randomId: String): Response<GifResponse> {
        return GifAPI.retrofitService.getGifs(randomId = randomId, limit = 25, page = page)
    }

    suspend fun getGifsForQuery(
        limit: Int = 25,
        query: String = "",
        page: Int = 0,
        randomId: String
    ): Response<GifResponse> {
        return GifAPI.retrofitService.getGifsForQuery(randomId = randomId, limit = 25, query = query, page = page)
    }
}