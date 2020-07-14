package com.example.moviehound.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "movies")
data class MovieModel(
    @PrimaryKey @SerializedName("id") val id: Int,
    @SerializedName("poster_path") val poster: String? = null,
    @SerializedName("backdrop_path") val backdrop: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("video") val video: Boolean,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("release_date") val releaseDate: String,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean = false
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieModel

        if (id != other.id) return false
        if (poster != other.poster) return false
        if (title != other.title) return false
        if (rating != other.rating) return false
        if (isFavorite != other.isFavorite) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + poster.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + rating.hashCode()
        result = 31 * result + isFavorite.hashCode()
        return result
    }
}