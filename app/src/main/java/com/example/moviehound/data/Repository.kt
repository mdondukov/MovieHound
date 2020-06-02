package com.example.moviehound.data

import com.example.moviehound.AppActivity
import com.example.moviehound.BuildConfig
import com.example.moviehound.ui.global.Api
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Repository {
    private val api: Api

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor()
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

        api = retrofit.create(Api::class.java)
    }

    fun getMovies(
        page: Int = 1,
        onSuccess: (movies: List<Movie>) -> Unit,
        onError: () -> Unit
    ) {
        api.getMovies(AppActivity.API_KEY, page)
            .enqueue(object : Callback<Movies> {
                override fun onResponse(
                    call: Call<Movies>,
                    response: Response<Movies>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.movies)
                        } else {
                            onError.invoke()
                        }
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getMovie(
        movieId: Int,
        onSuccess: (movie: Movie) -> Unit,
        onError: () -> Unit
    ) {
        api.getMovie(movieId, AppActivity.API_KEY)
            .enqueue(object : Callback<Movie> {
                override fun onResponse(
                    call: Call<Movie>,
                    response: Response<Movie>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val movie = Movie(
                                responseBody.id,
                                responseBody.poster,
                                responseBody.backdrop,
                                responseBody.title,
                                responseBody.originalTitle,
                                responseBody.tagline,
                                responseBody.genres,
                                responseBody.overview,
                                responseBody.video,
                                responseBody.rating,
                                responseBody.voteCount,
                                responseBody.releaseDate,
                                responseBody.productionCountries,
                                responseBody.runtime
                            )
                            onSuccess.invoke(movie)
                        } else {
                            onError.invoke()
                        }
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<Movie>, t: Throwable) {
                    onError.invoke()
                }
            })
    }
}