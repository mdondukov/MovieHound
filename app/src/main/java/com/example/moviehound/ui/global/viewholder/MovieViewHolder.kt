package com.example.moviehound.ui.global.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.moviehound.R
import com.example.moviehound.model.Movie

class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val posterIv: ImageView = itemView.findViewById(R.id.poster_iv)
    private val titleTv: TextView = itemView.findViewById(R.id.title_tv)
    private val ratingTv: TextView = itemView.findViewById(R.id.rating_tv)

    fun bind(item: Movie) {

        Glide.with(itemView)
            .load("https://image.tmdb.org/t/p/w342${item.poster}")
            .transform(CenterCrop())
            .into(posterIv)

        titleTv.text = item.title
        ratingTv.text = item.rating.toString()
    }
}