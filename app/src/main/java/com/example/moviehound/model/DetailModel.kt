package com.example.moviehound.model

import com.google.gson.annotations.SerializedName

data class DetailModel(
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("production_countries") val productionCountries: List<ProductionCountry>,
    @SerializedName("runtime") val runtime: Int,
    @SerializedName("budget") val budget: Int
)