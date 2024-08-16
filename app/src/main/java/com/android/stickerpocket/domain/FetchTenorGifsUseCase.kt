package com.android.stickerpocket.domain

import com.android.stickerpocket.network.TenorAPI
import com.android.stickerpocket.network.TenorServiceImpl
import com.android.stickerpocket.network.response.tenor.TenorGifs

class FetchTenorGifsUseCase {
    suspend fun execute(query: String = ""): TenorGifs? {
        try {
            val response = if (query.isEmpty()) TenorServiceImpl.getGifs(limit = 50)
            else TenorServiceImpl.getGifsForQuery(limit = 50, query = query)
            if (response.isSuccessful) return response.body()
        } catch (e: Exception) {
            println(e.localizedMessage)
        }
        return null
    }
}