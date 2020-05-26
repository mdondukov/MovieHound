package com.example.moviehound.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val id: Int,
    val posterResId: Int,
    val backdropResId: Int,
    val title: String,
    val originalTitle: String,
    val tagline: String,
    val genres: ArrayList<String>,
    val overview: String,
    val releaseDate: String,
    val productionCountries: ArrayList<String>,
    val runtime: Int,
    var comment: String
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Movie

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}