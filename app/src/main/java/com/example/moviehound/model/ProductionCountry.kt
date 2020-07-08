package com.example.moviehound.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductionCountry(
    @SerializedName("iso_3166_1") val iso: String,
    @SerializedName("name") val name: String
) : Parcelable