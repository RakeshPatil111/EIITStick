package com.android.stickerpocket.network

import com.android.stickerpocket.network.response.GifResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL =
    "https://api.giphy.com/v1/gifs/"

interface GifService {
    @GET("trending")
    suspend fun getGifs(
        @Query("api_key") apiKey: String = "UE20qxVTbBaoaVzWtCnKV5ZBuhEfuWkm",
        @Query("limit") limit: Int = 25,
        @Query("offset") page: Int = 0,
        @Query("random_id") randomId: String,
        @Query("rating") rating: String = "pg"
    ) : Response<GifResponse>

    @GET("search")
    suspend fun getGifsForQuery(
        @Query("api_key") apiKey: String = "UE20qxVTbBaoaVzWtCnKV5ZBuhEfuWkm",
        @Query("limit") limit: Int = 25,
        @Query("q") query: String = "",
        @Query("offset") page: Int = 0,
        @Query("random_id") randomId: String,
        @Query("rating") rating: String = "pg"
    ) : Response<GifResponse>
}

object GifAPI {
    val retrofitService: GifService by lazy {
        createRetrofit().create(GifService::class.java)
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