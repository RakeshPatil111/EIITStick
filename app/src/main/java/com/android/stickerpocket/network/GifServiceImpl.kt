package com.android.stickerpocket.network

import com.android.stickerpocket.network.response.GifResponse
import retrofit2.Response

object GifServiceImpl {
    suspend fun getGifs(page: Int = 0, randomId: String): Response<GifResponse> {
        return GifAPI.retrofitService.getGifs(randomId = randomId, limit = 25, page = page)
    }

    suspend fun getGifsForQuery(
        query: String = "",
        page: Int = 0,
        randomId: String
    ): Response<GifResponse> {
        return GifAPI.retrofitService.getGifsForQuery(randomId = randomId, limit = 25, query = query, page = page)
    }
}