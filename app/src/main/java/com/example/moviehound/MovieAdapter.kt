package com.example.moviehound

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private val mInflater: LayoutInflater,
    private var mMovieList: ArrayList<Movie>,
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

            val view = holder.itemView
            view.findViewById<Button>(R.id.detail_button).setOnClickListener {
                mListener(item)
                view.findViewById<TextView>(R.id.title_text_view)
                    .setTextColor(view.resources.getColor(R.color.colorAccent))
            }
            view.findViewById<ImageView>(R.id.favorite_image_view)
                .setOnClickListener { itemChanged(position) }
        }
    }

    fun getItems() : ArrayList<Movie> {
        return mMovieList
    }

    fun resetItems(movies: ArrayList<Movie>) {
        mMovieList = movies
        notifyDataSetChanged()
    }

    private fun itemChanged(position: Int) {
        mMovieList[position].mIsFavorite = !mMovieList[position].mIsFavorite
        notifyItemChanged(position)
    }
}