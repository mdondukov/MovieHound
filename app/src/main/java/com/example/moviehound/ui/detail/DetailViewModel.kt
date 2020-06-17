package com.example.moviehound.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviehound.api.ServiceGenerator
import com.example.moviehound.api.State
import com.example.moviehound.data.Movie
import com.example.moviehound.domain.MovieUseCase

class DetailViewModel : ViewModel() {
    private val movieUseCase = ServiceGenerator.movieUseCase
    private val movieLiveData = MutableLiveData<Movie>()
    private val errorLiveData = MutableLiveData<String>()

    val movie: LiveData<Movie>
        get() = movieLiveData

    val error: LiveData<String>
        get() = errorLiveData

    fun getMovie(movieId: Int) {
        movieUseCase.load(movieId, object : MovieUseCase.GetMovieCallback {
            override fun onSuccess(item: Movie) {
                movieLiveData.postValue(item)
            }

            override fun onError(error: String) {
                errorLiveData.postValue(error)
            }
        })
    }

    fun getNetworkState(): LiveData<State> {
        return movieUseCase.state
    }
}