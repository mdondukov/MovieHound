package com.example.moviehound.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.moviehound.api.NetworkState
import com.example.moviehound.data.MovieRepository
import com.example.moviehound.model.MovieModel
import com.example.moviehound.model.MovieResult

class MovieListViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val movieResult = MutableLiveData<MovieResult>()

    init {
        getMovies()
    }

    val movieList: LiveData<PagedList<MovieModel>> =
        Transformations.switchMap(movieResult) { it.data }

    val networkState: LiveData<NetworkState> =
        Transformations.switchMap(movieResult) { it.networkState }

    fun listIsEmpty(): Boolean {
        return movieList.value?.isEmpty() ?: true
    }

    fun getMovies() {
        movieResult.postValue(repository.movies())
    }
}

