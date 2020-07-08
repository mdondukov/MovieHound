package com.example.moviehound.model

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Genre(
    @PrimaryKey @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) : Parcelable