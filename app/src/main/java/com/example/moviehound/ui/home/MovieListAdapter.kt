package com.example.moviehound.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.R
import com.example.moviehound.model.MovieModel
import com.example.moviehound.ui.global.SharedViewModel
import com.example.moviehound.ui.global.viewholder.MovieViewHolder

class MovieListAdapter(
    private val inflater: LayoutInflater,
    private val sharedViewModel: SharedViewModel,
    private val listener: () -> Unit
) : PagedListAdapter<MovieModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder(
            inflater.inflate(
                R.layout.item_movie,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            val item = getItem(position)

            if (item != null) {
                holder.bind(item)

                val favoriteImageView: ImageView =
                    holder.itemView.findViewById(R.id.favorite_iv)

                favoriteImageView.setOnClickListener { switchFavoriteStatus(it, item) }

                holder.itemView.findViewById<View>(R.id.movie_layout)
                    .setOnClickListener {
                        sharedViewModel.selectMovie(item)
                        listener.invoke()
                    }
            }
        }
    }

    private fun switchFavoriteStatus(view: View, movie: MovieModel) {
        view.isSelected = !view.isSelected
        movie.isFavorite = !movie.isFavorite
        sharedViewModel.updateFavoriteStatus(movie)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieModel>() {
            override fun areItemsTheSame(oldItem: MovieModel, newItem: MovieModel) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MovieModel, newItem: MovieModel) = oldItem == newItem
        }
    }
}