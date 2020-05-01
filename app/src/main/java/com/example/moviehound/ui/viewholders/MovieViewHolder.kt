package com.example.moviehound.ui.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.R
import com.example.moviehound.data.Movie

class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mCoverImageView: ImageView = itemView.findViewById(R.id.cover_image_view)
    private val mTitleTextView: TextView = itemView.findViewById(R.id.title_text_view)

    fun bind(item: Movie) {
        mCoverImageView.setImageResource(item.mCoverResId)
        mTitleTextView.text = item.mTitle
    }
}