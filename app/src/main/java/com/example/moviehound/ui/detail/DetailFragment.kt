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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.moviehound.AppActivity
import com.example.moviehound.Injection
import com.example.moviehound.R
import com.example.moviehound.api.Status
import com.example.moviehound.model.DetailModel
import com.example.moviehound.model.MovieModel
import com.example.moviehound.ui.global.SharedViewModel
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var movie: MovieModel
    private lateinit var toolbar: Toolbar
    private lateinit var posterIv: ImageView
    private lateinit var backdropIv: ImageView
    private lateinit var titleTv: TextView
    private lateinit var originalTitleTv: TextView
    private lateinit var titleAndReleaseDateTv: TextView
    private lateinit var genresTv: TextView
    private lateinit var countryBudgetRuntimeTv: TextView
    private lateinit var ratingTv: TextView
    private lateinit var voteCountTv: TextView
    private lateinit var overviewTv: TextView
    private lateinit var trailerLl: LinearLayout
    private lateinit var favoriteLl: LinearLayout
    private lateinit var inviteLl: LinearLayout
    private lateinit var favoriteIv: ImageView
    private var movieId = 0

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

        sharedViewModel =
            ViewModelProvider(
                requireActivity(),
                Injection.provideShareViewModelFactory(requireContext())
            )
                .get(SharedViewModel::class.java)

        detailViewModel = ViewModelProvider(this, Injection.provideDetailViewModelFactory())
            .get(DetailViewModel::class.java)

        sharedViewModel.selected.observe(viewLifecycleOwner, Observer {
            showMovie(it)
            movieId = it.id
            detailViewModel.getDetails(movieId)
        })

        detailViewModel.movieDetail.observe(viewLifecycleOwner, Observer { showDetails(it) })

        initState()

        toolbar.setNavigationOnClickListener { fragmentManager?.popBackStack() }
        favoriteLl.setOnClickListener { switchFavoriteStatus() }
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
        titleAndReleaseDateTv = view.findViewById(R.id.title_and_release_date_tv)
        genresTv = view.findViewById(R.id.genres_tv)
        countryBudgetRuntimeTv = view.findViewById(R.id.country_budget_runtime_tv)
        ratingTv = view.findViewById(R.id.rating_tv)
        voteCountTv = view.findViewById(R.id.vote_count_tv)
        overviewTv = view.findViewById(R.id.overview_tv)
        trailerLl = view.findViewById(R.id.trailer_layout)
        favoriteLl = view.findViewById(R.id.favorite_layout)
        favoriteIv = view.findViewById(R.id.favorite_iv)
        inviteLl = view.findViewById(R.id.invite_layout)
    }

    private fun showMovie(item: MovieModel) {
        movie = item

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
        ratingTv.text = movie.rating.toString()
        voteCountTv.text = context?.resources?.getString(R.string.vote_count, movie.voteCount)
        titleAndReleaseDateTv.text = context?.resources?.getString(
            R.string.title_and_release_date,
            movie.title,
            (movie.releaseDate).substring(0, 4)
        )
        showFavoriteStatus()
    }

    private fun showFavoriteStatus() {
        favoriteIv.isSelected = movie.isFavorite
    }

    private fun showDetails(details: DetailModel) {
        genresTv.text =
            (details.genres.flatMap { listOf(it.name) }).joinToString(", ")

        countryBudgetRuntimeTv.text =
            if (details.budget != 0) {
                context?.resources?.getString(
                    R.string.country_budget_runtime,
                    (details.productionCountries.flatMap { listOf(it.name) }).joinToString(", "),
                    details.budget.div(1000000),
                    details.runtime
                )
            } else {
                context?.resources?.getString(
                    R.string.country_and_runtime,
                    (details.productionCountries.flatMap { listOf(it.name) }).joinToString(", "),
                    details.runtime
                )
            }
    }

    private fun initState() {
        detailViewModel.getNetworkState().observe(this.viewLifecycleOwner, Observer { state ->
            if (state?.status == Status.FAILED) state.msg?.let { showErrorSnackBar(it) }
        })
    }

    private fun switchFavoriteStatus() {
        movie.isFavorite = !movie.isFavorite
        sharedViewModel.updateFavoriteStatus(movie)
        showFavoriteStatus()
    }

    private fun showErrorSnackBar(msg: String) {
        Snackbar
            .make(this.requireView(), msg, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) {
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w1280${movie.backdrop}")
                    .transform(CenterCrop())
                    .into(backdropIv)

                detailViewModel.getDetails(movieId)
            }
            .show()
    }
}
