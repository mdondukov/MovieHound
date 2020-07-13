package com.example.moviehound.ui.global

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviehound.model.MovieModel

class SharedViewModel: ViewModel() {
    private val selectedMovieLiveData = MutableLiveData<MovieModel>()
    private val favoriteListLiveData = MutableLiveData<List<MovieModel>>()

    private val favorites = ArrayList<MovieModel>()

    val selected: LiveData<MovieModel>
        get() = selectedMovieLiveData

    fun selectMovie(movie: MovieModel) {
        selectedMovieLiveData.postValue(movie)
    }

    fun getFavoriteList(): LiveData<List<MovieModel>> {
        loadFavoriteList()
        return favoriteListLiveData
    }

    private fun loadFavoriteList() {
        favoriteListLiveData.postValue(favorites)
    }

    fun addFavoriteMovie(movie: MovieModel) {
        favorites.add(movie)
    }

    fun insertFavoriteMovie(index: Int, movie: MovieModel) {
        favorites.add(index, movie)
    }

    fun removeFavoriteMovie(movie: MovieModel) {
        favorites.remove(movie)
    }
}