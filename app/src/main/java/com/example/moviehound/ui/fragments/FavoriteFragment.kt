package com.example.moviehound.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.MainActivity
import com.example.moviehound.OnMovieListClickListener
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.example.moviehound.ui.adapters.FavoriteAdapter
import com.example.moviehound.ui.itemdecorations.MovieItemDecoration
import com.example.moviehound.util.doOnApplyWindowInsets

class FavoriteFragment : Fragment() {
    private lateinit var mFavoriteList: ArrayList<Movie>
    private lateinit var mAdapter: FavoriteAdapter
    private var listener: OnMovieListClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()

        val recycler = view.findViewById<RecyclerView>(R.id.favorite_movie_list)
        recycler.doOnApplyWindowInsets { recyclerView, insets, margin ->
            val params = recyclerView.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = margin.bottom + insets.systemWindowInsetBottom
            insets
        }
        initRecycler(recycler)
    }

    private fun setData() {
        arguments?.let {
            mFavoriteList = it.getParcelableArrayList(MainActivity.FAVORITE_LIST)!!
        }
    }

    private fun initRecycler(recycler: RecyclerView) {
        val gridColumnCount: Int = resources.getInteger(R.integer.grid_column_count)
        val layoutManager = GridLayoutManager(context, gridColumnCount)
        recycler.layoutManager = layoutManager

        mAdapter = FavoriteAdapter(
            LayoutInflater.from(context),
            mFavoriteList
        ) { listener?.onMovieClick(it) }

        recycler.adapter = mAdapter
        recycler.addItemDecoration(
            MovieItemDecoration(
                resources.getDimension(R.dimen.margin_sm).toInt()
            )
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is OnMovieListClickListener) listener = activity as OnMovieListClickListener
    }

    companion object {
        fun newInstance(favorites: ArrayList<Movie>): FavoriteFragment {
            val fragment = FavoriteFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(MainActivity.FAVORITE_LIST, favorites)
            fragment.arguments = bundle
            return fragment
        }
    }
}
