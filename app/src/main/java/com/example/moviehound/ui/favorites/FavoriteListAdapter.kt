package com.example.moviehound.ui.favorites

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.example.moviehound.ui.global.SharedViewModel
import com.example.moviehound.ui.global.viewholder.MovieViewHolder
import com.google.android.material.snackbar.Snackbar

class FavoriteListAdapter(
    private val inflater: LayoutInflater,
    private val sharedViewModel: SharedViewModel,
    private val listener: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MovieViewHolder(
            inflater.inflate(
                R.layout.item_movie,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            val item = differ.currentList[position]
            holder.bind(item)

            val favoriteImageView: ImageView =
                holder.itemView.findViewById(R.id.favorite_iv)

            if (differ.currentList.size != 0) favoriteImageView.isSelected = true

            favoriteImageView.setOnClickListener {
                it.isSelected = false
                val index = differ.currentList.indexOf(item)
                sharedViewModel.removeFavoriteMovie(item)
                notifyItemRemoved(index)
                getFavoriteRemoveSnackBar(holder, index, item)
            }

            holder.itemView.findViewById<View>(R.id.movie_layout)
                .setOnClickListener {
                    sharedViewModel.selectId(item.id)
                    listener.invoke()
                }
        }
    }

    fun submitList(movies: List<Movie>) = differ.submitList(movies)

    private fun getFavoriteRemoveSnackBar(
        holder: RecyclerView.ViewHolder,
        index: Int,
        item: Movie
    ) {
        holder.apply {
            var counter = 3
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
                        object : CountDownTimer(3000, 1000) {
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
                    sharedViewModel.insertFavoriteMovie(index, item)
                    notifyItemInserted(index)
                }
                show()
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem == newItem
        }

    }
}