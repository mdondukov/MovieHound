package com.example.moviehound.ui.global.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.R
import com.example.moviehound.data.Movie

class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val posterIv: ImageView = itemView.findViewById(R.id.poster_iv)
    private val titleTv: TextView = itemView.findViewById(R.id.title_tv)

    fun bind(item: Movie) {
        posterIv.setImageResource(item.posterResId)
        titleTv.text = item.title
    }
}