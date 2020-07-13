package com.example.moviehound.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.moviehound.api.NetworkState
import com.example.moviehound.data.MovieListRepository
import com.example.moviehound.model.MovieModel
import com.example.moviehound.model.MovieResult

class MovieListViewModel(
    private val repository: MovieListRepository
) : ViewModel() {

    private val movieResult = MutableLiveData<MovieResult>()

    init {
        retry()
    }

    val movieList: LiveData<PagedList<MovieModel>> =
        Transformations.switchMap(movieResult) { it.data }

    val networkState: LiveData<NetworkState> =
        Transformations.switchMap(movieResult) { it.networkState }

    fun listIsEmpty(): Boolean {
        return movieList.value?.isEmpty() ?: true
    }

    fun retry() {
        movieResult.postValue(repository.movies())
    }
}

