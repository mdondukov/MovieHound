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
import com.example.moviehound.Injection
import com.example.moviehound.R
import com.example.moviehound.api.NetworkState
import com.example.moviehound.api.Status
import com.example.moviehound.ui.global.OnMovieListClickListener
import com.example.moviehound.ui.global.SharedViewModel
import com.example.moviehound.ui.global.itemdecoration.MovieItemDecoration
import com.google.android.material.snackbar.Snackbar

class MovieListFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var movieListViewModel: MovieListViewModel
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
        progress = view.findViewById(R.id.progress_bar)

        sharedViewModel =
            ViewModelProvider(
                requireActivity(),
                Injection.provideShareViewModelFactory(requireContext())
            )
                .get(SharedViewModel::class.java)

        movieListViewModel =
            ViewModelProvider(this, Injection.provideMovieViewModelFactory(requireContext()))
                .get(MovieListViewModel::class.java)

        initRecycler(view)
        initState()
    }

    private fun initRecycler(view: View) {
        val gridColumnCount: Int = resources.getInteger(R.integer.grid_column_count)
        recycler = view.findViewById(R.id.movie_list)
        layoutManager = GridLayoutManager(context, gridColumnCount)
        recycler.layoutManager = layoutManager

        adapter = MovieListAdapter(
            LayoutInflater.from(context),
            sharedViewModel
        ) {
            listener?.onMovieClick()
        }

        recycler.adapter = adapter
        recycler.addItemDecoration(
            MovieItemDecoration(
                resources.getDimension(R.dimen.space_4).toInt()
            )
        )

        movieListViewModel.movieList.observe(
            this.viewLifecycleOwner,
            Observer {
                adapter.submitList(it)
            }
        )
    }

    private fun initState() {
        movieListViewModel.networkState.observe(this.viewLifecycleOwner, Observer { state ->
            progress.visibility =
                if (movieListViewModel.listIsEmpty() && state == NetworkState.LOADING) View.VISIBLE
                else View.GONE

            if (movieListViewModel.listIsEmpty() && state?.status == Status.FAILED)
                state.msg?.let { showErrorSnackBar(it) }
        })
    }

    private fun showErrorSnackBar(msg: String) {
        Snackbar
            .make(this.requireView(), msg, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) { movieListViewModel.getMovies() }
            .show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is OnMovieListClickListener) listener = activity as OnMovieListClickListener
    }
}