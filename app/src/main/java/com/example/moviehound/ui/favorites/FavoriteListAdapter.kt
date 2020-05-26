package com.example.moviehound.ui.favorites

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            holder.itemView.findViewById<View>(R.id.movie_layout)
                .setOnClickListener { listener(item) }
        }
    }

    private fun getFavoriteRemoveSnackbar(
        holder: RecyclerView.ViewHolder,
        index: Int,
        item: Movie
    ) {
        holder.apply {
            var counter = 5
            val snackbar = Snackbar.make(
                itemView,
                itemView.resources.getString(
                    R.string.favorite_remove,
                    counter.toString()
                ),
                Snackbar.LENGTH_INDEFINITE
            )
            val snCallback: Snackbar.Callback = object : Snackbar.Callback() {
                override fun onShown(sb: Snackbar?) {
                    sb?.apply {
                        object : CountDownTimer(5000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                setText(
                                    itemView.resources.getString(
                                        R.string.favorite_remove,
                                        counter.toString()
                                    )
                                )
                                counter--
                            }
                            override fun onFinish() {
                                dismiss()
                            }
                        }.start()
                    }
                }
            }

            snackbar.apply {
                addCallback(snCallback)
                setAction(itemView.context.resources.getString(R.string.cancel)) {
                    favoriteList.add(index, item)
                    notifyDataSetChanged()
                }
                show()
            }
        }
    }
}