package com.example.moviehound.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.AppActivity
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.example.moviehound.ui.global.OnMovieListClickListener
import com.example.moviehound.ui.global.itemdecoration.MovieItemDecoration

class FavoriteListFragment : Fragment() {
    private lateinit var favoriteList: ArrayList<Movie>
    private lateinit var adapter: FavoriteListAdapter
    private var listener: OnMovieListClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppActivity).setSupportActionBar(toolbar)

        setData()

        val recycler = view.findViewById<RecyclerView>(R.id.favorite_movie_list)
        initRecycler(recycler)
    }

    private fun setData() {
        arguments?.let {
            favoriteList =
                it.getParcelableArrayList<Movie>(AppActivity.FAVORITE_LIST) as ArrayList<Movie>
        }
    }

    private fun initRecycler(recycler: RecyclerView) {
        val gridColumnCount: Int = resources.getInteger(R.integer.grid_column_count)
        val layoutManager = GridLayoutManager(context, gridColumnCount)
        recycler.layoutManager = layoutManager

        adapter = FavoriteListAdapter(
            LayoutInflater.from(context),
            favoriteList
        ) { listener?.onMovieClick(it.id) }

        recycler.adapter = adapter
        recycler.addItemDecoration(
            MovieItemDecoration(
                resources.getDimension(R.dimen.space_4).toInt()
            )
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is OnMovieListClickListener) listener = activity as OnMovieListClickListener
    }

    companion object {
        fun newInstance(favorites: ArrayList<Movie>): FavoriteListFragment {
            val fragment = FavoriteListFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(AppActivity.FAVORITE_LIST, favorites)
            fragment.arguments = bundle
            return fragment
        }
    }
}
