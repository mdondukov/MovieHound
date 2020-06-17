package com.example.moviehound.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.example.moviehound.ui.global.viewholder.MovieViewHolder

class MovieListAdapter(
    private val inflater: LayoutInflater,
    private var favoriteList: ArrayList<Movie>,
    private val listener: (itemId: Int) -> Unit
) : PagedListAdapter<Movie, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

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

                if (favoriteList.isNotEmpty()) {
                    val itemExists: Boolean = checkAvailability(favoriteList, item)
                    if (itemExists) setFavoriteStatus(favoriteImageView, true)
                    else setFavoriteStatus(favoriteImageView, false)

                } else setFavoriteStatus(favoriteImageView, false)

                favoriteImageView.setOnClickListener {
                    if (it.isSelected) {
                        setFavoriteStatus(it, false)
                        favoriteList.remove(item)
                    } else {
                        setFavoriteStatus(it, true)
                        favoriteList.add(item)
                    }
                }

                holder.itemView.findViewById<View>(R.id.movie_layout)
                    .setOnClickListener { listener.invoke(item.id) }
            }
        }
    }

    private fun checkAvailability(favorites: ArrayList<Movie>, item: Movie): Boolean {
        for (movie: Movie in favorites) {
            if (movie.id == item.id) return true
        }
        return false
    }

    private fun setFavoriteStatus(view: View, status: Boolean) {
        view.isSelected = status
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
        }
    }
}