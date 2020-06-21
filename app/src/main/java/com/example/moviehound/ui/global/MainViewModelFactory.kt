package com.example.moviehound.ui.global

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviehound.ui.detail.DetailViewModel
import com.example.moviehound.ui.home.MovieListViewModel

class MainViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            SharedViewModel() as T
        } else if (modelClass.isAssignableFrom(MovieListViewModel::class.java)) {
            MovieListViewModel() as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            DetailViewModel() as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}