package com.example.moviehound

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    val mId: Int,
    val mCoverResId: Int,
    val mTitleResId: Int,
    val mDescResId: Int,
    var mIsFavorite: Boolean,
    var mComment: String
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Movie

        if (mId != other.mId) return false

        return true
    }

    override fun hashCode(): Int {
        return mId
    }
}