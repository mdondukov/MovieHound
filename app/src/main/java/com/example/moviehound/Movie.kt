package com.example.moviehound

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val mCoverResId: Int,
    val mTitleResId: Int,
    val mDesc: String,
    val mIsFavorite: Boolean,
    val mComment: String
) : Parcelable