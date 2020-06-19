package com.example.moviehound.ui.global

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviehound.data.Movie

class SharedViewModel: ViewModel() {
    private val selectedIdLiveData = MutableLiveData<Int>()
    private val favoriteListLiveData = MutableLiveData<List<Movie>>()

    private val favorites = ArrayList<Movie>()

    val selectedId: LiveData<Int>
        get() = selectedIdLiveData

    fun selectId(id: Int) {
        selectedIdLiveData.postValue(id)
    }

    fun getFavoriteList(): LiveData<List<Movie>> {
        loadFavoriteList()
        return favoriteListLiveData
    }

    private fun loadFavoriteList() {
        favoriteListLiveData.postValue(favorites)
    }

    fun addFavoriteMovie(movie: Movie) {
        favorites.add(movie)
    }

    fun insertFavoriteMovie(index: Int, movie: Movie) {
        favorites.add(index, movie)
    }

    fun removeFavoriteMovie(movie: Movie) {
        favorites.remove(movie)
    }
}