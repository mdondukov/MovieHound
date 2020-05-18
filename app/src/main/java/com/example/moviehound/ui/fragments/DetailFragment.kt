package com.example.moviehound.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.moviehound.MainActivity
import com.example.moviehound.R
import com.example.moviehound.data.Movie
import com.example.moviehound.util.doOnApplyWindowInsets
import com.google.android.material.textfield.TextInputEditText

class DetailFragment : Fragment() {
    private lateinit var mMovie: Movie
    private lateinit var mFavoriteList: ArrayList<Movie>
    private lateinit var mCoverImageView: ImageView
    private lateinit var mTitleTextView: TextView
    private lateinit var mDescTextView: TextView
    private lateinit var mFavoriteImageView: ImageView
    private lateinit var mInvitationImageView: ImageView
    private lateinit var mCommentEditText: TextInputEditText
    private var listener: OnMovieChanged? = null

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

        val rootView = view.findViewById<View>(R.id.detail_root_view)
        rootView.doOnApplyWindowInsets { recyclerView, insets, margin ->
            val params = recyclerView.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = margin.bottom + insets.systemWindowInsetBottom
            insets
        }

        mFavoriteImageView.setOnClickListener {
            if (it.isSelected) {
                setFavoriteStatus(it, false)
                mFavoriteList.remove(mMovie)
            } else {
                setFavoriteStatus(it, true)
                mFavoriteList.add(mMovie)
            }
        }

        mInvitationImageView.setOnClickListener {
            val title = mTitleTextView.text
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation, title))
            startActivity(intent)
        }
    }

    private fun initViews(view: View) {
        mCoverImageView = view.findViewById(R.id.cover_image_view)
        mTitleTextView = view.findViewById(R.id.title_text_view)
        mDescTextView = view.findViewById(R.id.desc_text_view)
        mFavoriteImageView = view.findViewById(R.id.favorite_image_view)
        mInvitationImageView = view.findViewById(R.id.invitation_image_view)
        mCommentEditText = view.findViewById(R.id.comment_edit_text)
    }

    private fun setData() {
        arguments?.let {
            mMovie = it.getParcelable(Movie::class.java.simpleName)!!
            mFavoriteList = it.getParcelableArrayList(MainActivity.FAVORITE_LIST)!!
        }

        mCoverImageView.setImageResource(mMovie.mCoverResId)
        mTitleTextView.text = mMovie.mTitle
        mDescTextView.text = mMovie.mDesc
        mCommentEditText.setText(mMovie.mComment)

        if (mFavoriteList.size != 0) {
            val itemExists: Boolean = checkAvailability(mFavoriteList, mMovie)
            if (itemExists) setFavoriteStatus(mFavoriteImageView, true)
            else setFavoriteStatus(mFavoriteImageView, false)

        } else setFavoriteStatus(mFavoriteImageView, false)
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
        listener?.setMovieResult(mMovie, mFavoriteList)
    }

    private fun saveComment() {
        val comment = mCommentEditText.text.toString()
        mMovie.mComment = comment
    }

    companion object {
        fun newInstance(movie: Movie, favorites: ArrayList<Movie>): DetailFragment {
            val fragment = DetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(Movie::class.java.simpleName, movie)
            bundle.putParcelableArrayList(MainActivity.FAVORITE_LIST, favorites)
            fragment.arguments = bundle
            return fragment
        }
    }

    interface OnMovieChanged {
        fun setMovieResult(movie: Movie, favorites: ArrayList<Movie>)
    }
}
