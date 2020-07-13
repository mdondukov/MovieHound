package com.example.moviehound.api

import com.example.moviehound.AppActivity
import com.example.moviehound.BuildConfig
import com.example.moviehound.model.DetailModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbService {
    @GET("discover/movie")
    fun getMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String = "ru"
    ): Call<MovieResponse>

    @GET("movie/{movie_id}")
    fun getDetail(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru"
    ): Call<DetailModel>

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/"

        fun create(): TmdbService {
            val client = OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .apply {
                            if (BuildConfig.DEBUG) {
                                level = HttpLoggingInterceptor.Level.BASIC
                            }
                        })
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(TmdbService::class.java)
        }
    }
}

fun getMovies(
    service: TmdbService,
    page: Int,
    onSuccess: (movieResponse: MovieResponse) -> Unit,
    onError: (error: String) -> Unit
) {
    service.getMovies(AppActivity.API_KEY, page)
        .enqueue(object : Callback<MovieResponse> {
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                onError(t.localizedMessage ?: "unknown error")
            }

            override fun onResponse(
                call: Call<MovieResponse>,
                response: Response<MovieResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body() ?: MovieResponse()
                    onSuccess(data)
                } else {
                    onError(response.errorBody()?.string() ?: "unknown error")
                }
            }

        })
}