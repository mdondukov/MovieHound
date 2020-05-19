package com.example.moviehound.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.example.moviehound.ui.global.viewholder.MovieViewHolder

class MovieListAdapter(
    private val inflater: LayoutInflater,
    private var movieList: ArrayList<Movie>,
    private var favoriteList: ArrayList<Movie>,
    private val listener: (movie: Movie) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder(
            inflater.inflate(
                R.layout.item_movie,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = movieList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            val item = movieList[position]
            holder.bind(item)

            val favoriteImageView: ImageView =
                holder.itemView.findViewById(R.id.favorite_iv)

            if (favoriteList.size != 0) {
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

            holder.itemView.findViewById<Button>(R.id.detail_button)
                .setOnClickListener { listener.invoke(item) }
        }
    }

    private fun checkAvailability(favorites: ArrayList<Movie>, item: Movie): Boolean {
        for (movie: Movie in favorites) {
            if (movie == item) return true
        }
        return false
    }

    private fun setFavoriteStatus(view: View, status: Boolean) {
        view.isSelected = status
    }
}