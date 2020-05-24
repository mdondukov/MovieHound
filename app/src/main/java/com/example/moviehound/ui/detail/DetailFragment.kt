package com.example.moviehound.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.moviehound.AppActivity
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.google.android.material.textfield.TextInputEditText

class DetailFragment : Fragment() {
    private lateinit var movie: Movie
    private lateinit var favoriteList: ArrayList<Movie>
    private lateinit var toolbar: Toolbar
    private lateinit var posterIv: ImageView
    private lateinit var titleTv: TextView
    private lateinit var overviewTv: TextView
    private lateinit var favoriteIv: ImageView
    private lateinit var inviteIv: ImageView
    private lateinit var commentEt: TextInputEditText
    private var listener: OnMovieChanged? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.statusBarColor = resources.getColor(R.color.black_20)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setData()

        toolbar.setNavigationOnClickListener {
            fragmentManager?.popBackStack()
        }

        favoriteIv.setOnClickListener {
            if (it.isSelected) {
                setFavoriteStatus(it, false)
                favoriteList.remove(movie)
            } else {
                setFavoriteStatus(it, true)
                favoriteList.add(movie)
            }
        }

        inviteIv.setOnClickListener {
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
        titleTv = view.findViewById(R.id.title_tv)
        overviewTv = view.findViewById(R.id.overview_tv)
        favoriteIv = view.findViewById(R.id.favorite_iv)
        inviteIv = view.findViewById(R.id.invite_iv)
        commentEt = view.findViewById(R.id.comment_et)
    }

    private fun setData() {
        arguments?.let {
            movie = it.getParcelable<Movie>(Movie::class.java.simpleName) as Movie
            favoriteList =
                it.getParcelableArrayList<Movie>(AppActivity.FAVORITE_LIST) as ArrayList<Movie>
        }
        posterIv.setImageResource(movie.posterResId)
        titleTv.text = movie.title
        overviewTv.text = movie.overview
        commentEt.setText(movie.comment)

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
        saveComment()
        listener?.setMovieResult(movie, favoriteList)
    }

    private fun saveComment() {
        val comment = commentEt.text.toString()
        movie.comment = comment
    }

    companion object {
        fun newInstance(item: Movie, favorites: ArrayList<Movie>): DetailFragment {
            val fragment = DetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(Movie::class.java.simpleName, item)
            bundle.putParcelableArrayList(AppActivity.FAVORITE_LIST, favorites)
            fragment.arguments = bundle
            return fragment
        }
    }

    interface OnMovieChanged {
        fun setMovieResult(item: Movie, favorites: ArrayList<Movie>)
    }
}
