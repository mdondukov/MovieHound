package com.example.moviehound.ui.global

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviehound.data.MovieRepository
import com.example.moviehound.model.MovieModel

class SharedViewModel(
    private val repository: MovieRepository
) : ViewModel() {
    private val selectedMovieLiveData = MutableLiveData<MovieModel>()

    val selected: LiveData<MovieModel>
        get() = selectedMovieLiveData

    fun selectMovie(movie: MovieModel) {
        selectedMovieLiveData.postValue(movie)
    }

    fun getFavoriteList(): LiveData<List<MovieModel>> {
        return repository.favorites()
    }

    fun updateFavoriteStatus(movie: MovieModel) {
        repository.update(movie)
    }
}