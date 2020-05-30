package com.example.moviehound.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.moviehound.AppActivity
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.example.moviehound.data.Repository
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {
    private lateinit var movie: Movie
    private lateinit var favoriteList: ArrayList<Movie>
    private lateinit var toolbar: Toolbar
    private lateinit var progress: View
    private lateinit var posterIv: ImageView
    private lateinit var backdropIv: ImageView
    private lateinit var titleTv: TextView
    private lateinit var originalTitleTv: TextView
    private lateinit var taglineTv: TextView
    private lateinit var genresTv: TextView
    private lateinit var metainfoTv: TextView
    private lateinit var ratingTv: TextView
    private lateinit var voteCountTv: TextView
    private lateinit var overviewTv: TextView
    private lateinit var trailerLl: LinearLayout
    private lateinit var favoriteLl: LinearLayout
    private lateinit var inviteLl: LinearLayout
    private lateinit var favoriteIv: ImageView

    private var movieId: Int = 0
    private var listener: OnMovieChanged? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor =
            requireContext().resources.getColor(R.color.black_20)

        initViews(view)

        arguments?.let {
            movieId = it.getInt(Movie::class.java.simpleName)
        }

        Repository.getMovie(
            movieId,
            onSuccess = { item ->
                progress.visibility = View.GONE
                setData(item)
            },
            onError = {
                Snackbar.make(
                    this.requireView(),
                    getString(R.string.error_fetch_movies),
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        )

        toolbar.setNavigationOnClickListener { fragmentManager?.popBackStack() }

        favoriteLl.setOnClickListener {
            if (favoriteIv.isSelected) {
                setFavoriteStatus(favoriteIv, false)
                favoriteList.remove(movie)
            } else {
                setFavoriteStatus(favoriteIv, true)
                favoriteList.add(movie)
            }
        }

        inviteLl.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                resources.getString(R.string.invitation, movie.title)
            )
            startActivity(intent)
        }
    }

    private fun initViews(view: View) {
        progress = view.findViewById(R.id.progress_layout)
        toolbar = view.findViewById(R.id.toolbar_detail)
        (activity as AppActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        posterIv = view.findViewById(R.id.poster_iv)
        backdropIv = view.findViewById(R.id.backdrop_iv)
        titleTv = view.findViewById(R.id.title_tv)
        originalTitleTv = view.findViewById(R.id.original_title_tv)
        taglineTv = view.findViewById(R.id.tagline_tv)
        genresTv = view.findViewById(R.id.genres_tv)
        metainfoTv = view.findViewById(R.id.metainfo_tv)
        ratingTv = view.findViewById(R.id.rating_tv)
        voteCountTv = view.findViewById(R.id.vote_count_tv)
        overviewTv = view.findViewById(R.id.overview_tv)
        trailerLl = view.findViewById(R.id.trailer_layout)
        favoriteLl = view.findViewById(R.id.favorite_layout)
        favoriteIv = view.findViewById(R.id.favorite_iv)
        inviteLl = view.findViewById(R.id.invite_layout)
    }

    private fun setData(item: Movie) {
        movie = item
        arguments?.let {
            favoriteList =
                it.getParcelableArrayList<Movie>(AppActivity.FAVORITE_LIST) as ArrayList<Movie>
        }

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w342${movie.poster}")
            .transform(CenterCrop())
            .into(posterIv)

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w1280${movie.backdrop}")
            .transform(CenterCrop())
            .into(backdropIv)

        titleTv.text = movie.title
        originalTitleTv.text = movie.originalTitle
        overviewTv.text = movie.overview

        if (movie.tagline.isEmpty())
            taglineTv.visibility = View.GONE
        else
            taglineTv.text = movie.tagline

        genresTv.text =
            (movie.genres.flatMap { listOf(it.name) }).joinToString(", ")

        metainfoTv.text = context?.resources?.getString(
            R.string.metainfo,
            (movie.releaseDate).substring(0, 4),
            (movie.productionCountries.flatMap { listOf(it.name) }).joinToString(", "),
            movie.runtime
        )

        ratingTv.text = movie.rating.toString()
        voteCountTv.text = context?.resources?.getString(R.string.vote_count, movie.voteCount)

        if (favoriteList.size != 0) {
            val itemExists: Boolean = checkAvailability(favoriteList, movie)
            if (itemExists) setFavoriteStatus(favoriteIv, true)
            else setFavoriteStatus(favoriteIv, false)

        } else setFavoriteStatus(favoriteIv, false)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is OnMovieChanged) listener = activity as OnMovieChanged
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.setMovieResult(favoriteList)
    }

    companion object {
        fun newInstance(itemId: Int, favorites: ArrayList<Movie>): DetailFragment {
            val fragment = DetailFragment()
            val bundle = Bundle()
            bundle.putInt(Movie::class.java.simpleName, itemId)
            bundle.putParcelableArrayList(AppActivity.FAVORITE_LIST, favorites)
            fragment.arguments = bundle
            return fragment
        }
    }

    interface OnMovieChanged {
        fun setMovieResult(favorites: ArrayList<Movie>)
    }
}
