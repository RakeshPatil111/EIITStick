package com.android.stickerpocket.network

import com.android.stickerpocket.network.response.GifResponse
import com.android.stickerpocket.network.response.tenor.TenorGifs
import retrofit2.Response

object TenorServiceImpl {
    suspend fun getGifs(limit: Int = 25, query: String = "", page: Int = 0): Response<TenorGifs> {
        return TenorAPI.retrofitService.getGifs(limit = limit)
    }

    suspend fun getGifsForQuery(
        limit: Int = 25,
        query: String = "",
        page: Int = 0
    ): Response<TenorGifs> {
        return TenorAPI.retrofitService.getGifsForQuery(limit = limit, query = query)
    }
}