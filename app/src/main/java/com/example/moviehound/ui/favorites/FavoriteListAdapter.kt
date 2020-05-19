package com.example.moviehound.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.example.moviehound.ui.global.viewholder.MovieViewHolder
import com.google.android.material.snackbar.Snackbar

class FavoriteListAdapter(
    private val inflater: LayoutInflater,
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

    override fun getItemCount() = favoriteList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            val item = favoriteList[position]
            holder.bind(item)

            val favoriteImageView: ImageView =
                holder.itemView.findViewById(R.id.favorite_iv)

            if (favoriteList.size != 0) favoriteImageView.isSelected = true

            favoriteImageView.setOnClickListener {
                it.isSelected = false
                val index = favoriteList.indexOf(item)
                favoriteList.remove(item)
                notifyDataSetChanged()
                getFavoriteRemoveSnackbar(holder, index, item)
            }

            holder.itemView.findViewById<Button>(R.id.detail_button)
                .setOnClickListener { listener(item) }
        }
    }

    private fun getFavoriteRemoveSnackbar(
        holder: RecyclerView.ViewHolder,
        index: Int,
        item: Movie
    ) {
        val view = holder.itemView
        Snackbar.make(
            view,
            view.context.resources.getString(R.string.favorite_removed),
            Snackbar.LENGTH_LONG
        )
            .setAction(view.context.resources.getString(R.string.cancel)) {
                favoriteList.add(index, item)
                notifyDataSetChanged()
            }
            .show()
    }
}