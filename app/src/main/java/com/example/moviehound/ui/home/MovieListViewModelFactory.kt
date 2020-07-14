package com.example.moviehound.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviehound.data.MovieRepository

class MovieListViewModelFactory(
    private val repository: MovieRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieListViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel Not Found")
    }
}