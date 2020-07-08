package com.example.moviehound.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviehound.api.ServiceGenerator
import com.example.moviehound.api.State
import com.example.moviehound.data.MovieUseCase
import com.example.moviehound.model.Movie

class DetailViewModel : ViewModel() {
    private val movieUseCase = ServiceGenerator.movieUseCase
    private val movieLiveData = MutableLiveData<Movie>()

    val movie: LiveData<Movie>
        get() = movieLiveData

    fun getMovie(movieId: Int) {
        movieUseCase.load(movieId, object : MovieUseCase.GetMovieCallback {
            override fun onSuccess(item: Movie) {
                movieLiveData.postValue(item)
            }
        })
    }

    fun getNetworkState(): LiveData<State> {
        return movieUseCase.state
    }
}