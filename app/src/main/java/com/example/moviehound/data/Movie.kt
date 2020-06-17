package com.example.moviehound.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("poster_path") val poster: String,
    @SerializedName("backdrop_path") val backdrop: String,
    @SerializedName("title") val title: String,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("tagline") val tagline: String,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("overview") val overview: String,
    @SerializedName("video") val video: Boolean,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("production_countries") val productionCountries: ArrayList<ProductionCountry>,
    @SerializedName("runtime") val runtime: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Movie

        if (id != other.id) return false
        if (poster != other.poster) return false
        if (backdrop != other.backdrop) return false
        if (title != other.title) return false
        if (originalTitle != other.originalTitle) return false
        if (tagline != other.tagline) return false
        if (genres != other.genres) return false
        if (overview != other.overview) return false
        if (video != other.video) return false
        if (rating != other.rating) return false
        if (voteCount != other.voteCount) return false
        if (releaseDate != other.releaseDate) return false
        if (productionCountries != other.productionCountries) return false
        if (runtime != other.runtime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + poster.hashCode()
        result = 31 * result + backdrop.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + originalTitle.hashCode()
        result = 31 * result + tagline.hashCode()
        result = 31 * result + genres.hashCode()
        result = 31 * result + overview.hashCode()
        result = 31 * result + video.hashCode()
        result = 31 * result + rating.hashCode()
        result = 31 * result + voteCount
        result = 31 * result + releaseDate.hashCode()
        result = 31 * result + productionCountries.hashCode()
        result = 31 * result + runtime
        return result
    }
}