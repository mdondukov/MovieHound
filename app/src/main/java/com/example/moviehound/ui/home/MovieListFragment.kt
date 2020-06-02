package com.example.moviehound.ui.home

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
import com.example.moviehound.data.Repository
import com.example.moviehound.ui.global.OnMovieListClickListener
import com.example.moviehound.ui.global.itemdecoration.MovieItemDecoration
import com.google.android.material.snackbar.Snackbar

class MovieListFragment : Fragment() {
    private lateinit var movieList: ArrayList<Movie>
    private lateinit var favoriteList: ArrayList<Movie>
    private lateinit var recycler: RecyclerView
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter: MovieListAdapter
    private lateinit var progress: View
    private var listener: OnMovieListClickListener? = null
    private var page = 1

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

        movieList = ArrayList()
        progress = view.findViewById(R.id.progress_bar)

        setData()
        initRecycler(view)
    }

    private fun setData() {
        arguments?.let {
            if (it.containsKey(AppActivity.MOVIE_LIST)) {
                movieList =
                    it.getParcelableArrayList<Movie>(AppActivity.MOVIE_LIST) as ArrayList<Movie>
            }

            if (it.containsKey(AppActivity.CURRENT_PAGE)) {
                page = it.getInt(AppActivity.CURRENT_PAGE)
            }

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
            mutableListOf(),
            favoriteList
        ) { listener?.onMovieClick(it) }

        recycler.adapter = adapter
        recycler.addItemDecoration(
            MovieItemDecoration(
                resources.getDimension(R.dimen.space_4).toInt()
            )
        )

        if (movieList.isEmpty())
            getMovies()
        else {
            progress.visibility = View.GONE
            adapter.appendMovies(movieList)
            attachLatestMoviesOnScrollListener()
        }
    }

    private fun getMovies() {
        Repository.getMovies(
            page,
            onSuccess = ::onMoviesFetched,
            onError = ::onError
        )
    }

    private fun onMoviesFetched(movies: List<Movie>) {
        progress.visibility = View.GONE
        adapter.appendMovies(movies)
        attachLatestMoviesOnScrollListener()
    }

    private fun onError() {
        Snackbar.make(
            this.requireView(),
            getString(R.string.error_fetch_movies),
            Snackbar.LENGTH_INDEFINITE
        ).show()
    }

    private fun attachLatestMoviesOnScrollListener() {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    recycler.removeOnScrollListener(this)
                    page++
                    getMovies()
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is OnMovieListClickListener) listener = activity as OnMovieListClickListener
    }

    override fun onStop() {
        super.onStop()
        this.arguments?.apply {
            putParcelableArrayList(AppActivity.MOVIE_LIST, (adapter.getMovies() as ArrayList))
            putInt(AppActivity.CURRENT_PAGE, page)
        }
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