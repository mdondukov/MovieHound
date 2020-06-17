package com.example.moviehound.api

import com.example.moviehound.BuildConfig
import com.example.moviehound.domain.MovieUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {
    val networkService: NetworkService
    val movieUseCase: MovieUseCase

    init {
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
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        networkService = retrofit.create(NetworkService::class.java)
        movieUseCase = MovieUseCase(networkService)
    }
}