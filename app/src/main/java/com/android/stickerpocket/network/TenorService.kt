package com.android.stickerpocket.network

import com.android.stickerpocket.network.response.GifResponse
import com.android.stickerpocket.network.response.tenor.TenorGifs
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://g.tenor.com/v1/"

interface TenorService {
    @GET("trending")
    suspend fun getGifs(
        @Query("key") apiKey: String = "LIVDSRZULELA",
        @Query("limit") limit: Int = 25,
    ) : Response<TenorGifs>

    @GET("search")
    suspend fun getGifsForQuery(
        @Query("key") apiKey: String = "LIVDSRZULELA",
        @Query("limit") limit: Int = 25,
        @Query("q") query: String
    ) : Response<TenorGifs>
}

object TenorAPI {
    val retrofitService: TenorService by lazy {
        createRetrofit().create(TenorService::class.java)
    }

    private fun createRetrofit(): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}