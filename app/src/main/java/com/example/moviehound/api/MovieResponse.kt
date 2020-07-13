package com.example.moviehound.api

import com.example.moviehound.model.MovieModel
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("page") val page: Int = 0,
    @SerializedName("results") val movies: List<MovieModel>? = null,
    @SerializedName("total_pages") val pages: Int = 0
)