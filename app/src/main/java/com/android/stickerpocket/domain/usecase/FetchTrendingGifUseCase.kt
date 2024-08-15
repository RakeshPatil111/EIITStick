package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.network.GifServiceImpl
import com.android.stickerpocket.network.response.GifResponse

class FetchTrendingGifUseCase {
    suspend fun execute(randomId: String, query: String = "", page: Int = 0): GifResponse? {
        val response = if (query.isEmpty()) GifServiceImpl.getGifs(randomId = randomId, page = page)
        else GifServiceImpl.getGifsForQuery(randomId = randomId,page = page)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}