package com.example.moviehound.api

import com.example.moviehound.model.Movie
import com.example.moviehound.model.Movies
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {
    @GET("discover/movie")
    fun getMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String = "ru"
    ): Call<Movies>

    @GET("movie/{movie_id}")
    fun getMovie(
        @Path("movie_id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "ru"
    ): Call<Movie>
}