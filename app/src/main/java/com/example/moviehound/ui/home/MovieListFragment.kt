package com.example.moviehound.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.AppActivity
import com.example.moviehound.R
import com.example.moviehound.api.State
import com.example.moviehound.data.Movie
import com.example.moviehound.ui.global.OnMovieListClickListener
import com.example.moviehound.ui.global.itemdecoration.MovieItemDecoration
import com.google.android.material.snackbar.Snackbar

class MovieListFragment : Fragment() {
    private lateinit var favoriteList: ArrayList<Movie>
    private lateinit var viewModel: MovieListViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter: MovieListAdapter
    private lateinit var progress: View
    private var listener: OnMovieListClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        (activity as AppActivity).setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(this).get(MovieListViewModel::class.java)
        progress = view.findViewById(R.id.progress_bar)

        setData()
        initRecycler(view)
        initState()
    }

    private fun setData() {
        arguments?.let {
            favoriteList =
                it.getParcelableArrayList<Movie>(AppActivity.FAVORITE_LIST) as ArrayList<Movie>
        }
    }

    private fun initRecycler(view: View) {
        val gridColumnCount: Int = resources.getInteger(R.integer.grid_column_count)
        recycler = view.findViewById(R.id.movie_list)
        layoutManager = GridLayoutManager(context, gridColumnCount)
        recycler.layoutManager = layoutManager

        adapter = MovieListAdapter(
            LayoutInflater.from(context),
            favoriteList
        ) { listener?.onMovieClick(it) }

        recycler.adapter = adapter
        recycler.addItemDecoration(
            MovieItemDecoration(
                resources.getDimension(R.dimen.space_4).toInt()
            )
        )

        viewModel.movieList.observe(
            this.viewLifecycleOwner,
            Observer { adapter.submitList(it) }
        )
    }

    private fun initState() {
        viewModel.getNetworkState().observe(this.viewLifecycleOwner, Observer { state ->
            progress.visibility =
                if (viewModel.listIsEmpty() && state == State.LOADING) View.VISIBLE
                else View.GONE
            if (viewModel.listIsEmpty() && state == State.ERROR) {
                showErrorSnackBar()
            }
        })
    }

    private fun showErrorSnackBar() {
        Snackbar.make(
            this.requireView(),
            resources.getString(R.string.error_fetch_movies),
            Snackbar.LENGTH_INDEFINITE
        ).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is OnMovieListClickListener) listener = activity as OnMovieListClickListener
    }

    companion object {
        fun newInstance(favorites: ArrayList<Movie>): MovieListFragment {
            val fragment = MovieListFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(AppActivity.FAVORITE_LIST, favorites)
            fragment.arguments = bundle
            return fragment
        }
    }
}