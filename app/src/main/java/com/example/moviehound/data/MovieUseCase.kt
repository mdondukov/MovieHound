package com.example.moviehound.data

import androidx.lifecycle.MutableLiveData
import com.example.moviehound.AppActivity
import com.example.moviehound.api.NetworkService
import com.example.moviehound.api.State
import com.example.moviehound.model.Movie
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieUseCase(
    private val networkService: NetworkService
) {
    val state = MutableLiveData<State>()

    fun load(movieId: Int, callback: GetMovieCallback) {
        updateState(State.LOADING)
        networkService.getMovie(movieId, AppActivity.API_KEY)
            .enqueue(object : Callback<Movie> {
                override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        if (data != null) {
                            val item = Movie(
                                data.id,
                                data.poster,
                                data.backdrop,
                                data.title,
                                data.originalTitle,
                                data.tagline,
                                data.genres,
                                data.overview,
                                data.video,
                                data.rating,
                                data.voteCount,
                                data.releaseDate,
                                data.productionCountries,
                                data.runtime
                            )
                            callback.onSuccess(item)
                            updateState(State.DONE)
                        }
                    } else {
                        updateState(State.ERROR)
                    }
                }

                override fun onFailure(call: Call<Movie>, t: Throwable) {
                    updateState(State.FAIL)
                }
            })
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }

    interface GetMovieCallback {
        fun onSuccess(item: Movie)
    }
}