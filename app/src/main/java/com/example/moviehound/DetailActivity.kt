package com.example.moviehound

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.moviehound.data.Movie
import com.google.android.material.textfield.TextInputEditText

class DetailActivity : AppCompatActivity() {
    private lateinit var mSettings: SharedPreferences
    private lateinit var mMovie: Movie
    private lateinit var mFavoriteList: ArrayList<Movie>
    private lateinit var mCoverImageView: ImageView
    private lateinit var mTitleTextView: TextView
    private lateinit var mDescTextView: TextView
    private lateinit var mFavoriteImageView: ImageView
    private lateinit var mCommentEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        mSettings = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        ThemeChanger.onActivityCreateSetTheme(this, mSettings)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initViews()
        setData()
    }

    private fun initViews() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mainLayout: ScrollView = findViewById(R.id.main_layout)
        setInsets(toolbar)
        setInsets(mainLayout)
        mCoverImageView = findViewById(R.id.cover_image_view)
        mTitleTextView = findViewById(R.id.title_text_view)
        mDescTextView = findViewById(R.id.desc_text_view)
        mFavoriteImageView = findViewById(R.id.favorite_image_view)
        mCommentEditText = findViewById(R.id.comment_edit_text)
    }

    private fun setData() {
        intent?.let {
            mMovie = it.getParcelableExtra(Movie::class.java.simpleName)!!
            mFavoriteList = it.getParcelableArrayListExtra(MainActivity.FAVORITE_LIST)!!
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveComment()
        outState.putParcelable(Movie::class.java.simpleName, mMovie)
        outState.putParcelableArrayList(MainActivity.FAVORITE_LIST, mFavoriteList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mMovie = savedInstanceState.getParcelable(Movie::class.java.simpleName)!!
        mFavoriteList = savedInstanceState.getParcelableArrayList(MainActivity.FAVORITE_LIST)!!
    }

    fun onFavoriteClick(view: View) {
        if (view.isSelected) {
            setFavoriteStatus(view, false)
            mFavoriteList.remove(mMovie)
        } else {
            setFavoriteStatus(view, true)
            mFavoriteList.add(mMovie)
        }
    }

    fun onInvitationClick(view: View) {
        val title = mTitleTextView.text
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation, title))
        startActivity(intent)
    }

    private fun setInsets(toolbar: Toolbar) {
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = insets.systemWindowInsetTop
            insets
        }
    }

    private fun setInsets(mainLayout: ScrollView) {
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            insets
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        saveComment()
        val data = Intent()
        data.putExtra(Movie::class.java.simpleName, mMovie)
        data.putParcelableArrayListExtra(MainActivity.FAVORITE_LIST, mFavoriteList)
        setResult(Activity.RESULT_OK, data)
        super.onBackPressed()
    }

    private fun saveComment() {
        val comment = mCommentEditText.text.toString()
        mMovie.mComment = comment
    }
}
