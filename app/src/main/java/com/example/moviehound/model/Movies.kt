package com.example.moviehound.model

import com.google.gson.annotations.SerializedName

data class Movies(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: List<Movie>,
    @SerializedName("total_pages") val pages: Int
)