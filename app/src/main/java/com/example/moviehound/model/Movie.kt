package com.example.moviehound.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.moviehound.db.GenresConverter
import com.example.moviehound.db.ProductionCountriesConverter
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey @SerializedName("id") val id: Int,
    @SerializedName("poster_path") val poster: String,
    @SerializedName("backdrop_path") val backdrop: String,
    @SerializedName("title") val title: String,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("tagline") val tagline: String,
    @TypeConverters(GenresConverter::class) @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("overview") val overview: String,
    @SerializedName("video") val video: Boolean,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("release_date") val releaseDate: String,
    @TypeConverters(ProductionCountriesConverter::class)
    @SerializedName("production_countries") val productionCountries: List<ProductionCountry>,
    @SerializedName("runtime") val runtime: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Movie

        if (id != other.id) return false
        if (poster != other.poster) return false
        if (title != other.title) return false
        if (rating != other.rating) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + poster.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + rating.hashCode()
        return result
    }
}