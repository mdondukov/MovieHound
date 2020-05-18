package com.example.moviehound.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.example.moviehound.ui.viewholders.MovieViewHolder

class FavoriteAdapter(
    private val mInflater: LayoutInflater,
    private var mFavoriteList: ArrayList<Movie>,
    private val mListener: (movie: Movie) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder(
            mInflater.inflate(
                R.layout.item_movie,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = mFavoriteList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            val item = mFavoriteList[position]
            holder.bind(item)

            val favoriteImageView: ImageView =
                holder.itemView.findViewById(R.id.favorite_image_view)

            if (mFavoriteList.size != 0) favoriteImageView.isSelected = true

            favoriteImageView.setOnClickListener {
                    it.isSelected = false
                    mFavoriteList.remove(item)
                    notifyItemRemoved(position)
                }

            holder.itemView.findViewById<Button>(R.id.detail_button)
                .setOnClickListener { mListener(item) }
        }
    }
}