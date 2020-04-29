package com.example.moviehound

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private val mInflater: LayoutInflater,
    private var mMovieList: ArrayList<Movie>,
    private var mFavoriteList: ArrayList<Movie>,
    private val mListener: (movie: Movie) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder(mInflater.inflate(R.layout.item_movie, parent, false))
    }

    override fun getItemCount() = mMovieList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            val item = mMovieList[position]
            holder.bind(item)

            val favoriteImageView: ImageView =
                holder.itemView.findViewById(R.id.favorite_image_view)

            if (mFavoriteList.size != 0) {
                val itemExists: Boolean = checkAvailability(mFavoriteList, item)
                if (itemExists) setFavoriteStatus(favoriteImageView, true)
                else setFavoriteStatus(favoriteImageView, false)

            } else setFavoriteStatus(favoriteImageView, false)

            favoriteImageView.setOnClickListener {
                if (it.isSelected) {
                    setFavoriteStatus(it, false)
                    mFavoriteList.remove(item)
                } else {
                    setFavoriteStatus(it, true)
                    mFavoriteList.add(item)
                }
            }

            holder.itemView.findViewById<Button>(R.id.detail_button)
                .setOnClickListener { mListener(item) }
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

    fun getItems(): ArrayList<Movie> {
        return mMovieList
    }

    fun resetItems(movies: ArrayList<Movie>, favorites: ArrayList<Movie>) {
        mMovieList = movies
        mFavoriteList = favorites
        notifyDataSetChanged()
    }
}