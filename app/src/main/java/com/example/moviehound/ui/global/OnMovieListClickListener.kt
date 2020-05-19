package com.example.moviehound.ui.global

import com.example.moviehound.data.Movie

interface OnMovieListClickListener {
    fun onMovieClick(item: Movie)
}