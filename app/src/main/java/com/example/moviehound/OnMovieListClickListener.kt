package com.example.moviehound

import com.example.moviehound.data.Movie

interface OnMovieListClickListener {
    fun onMovieClick(movie: Movie)
}