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
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var mSettings: SharedPreferences
    private lateinit var mMovie: Movie
    private lateinit var mCoverImageView: ImageView
    private lateinit var mTitleTextView: TextView
    private lateinit var mDescTextView: TextView
    private lateinit var mFavoriteImageView: ImageView
    private lateinit var mCommentEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        mSettings = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        Utils.onActivityCreateSetTheme(this, mSettings)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = insets.systemWindowInsetTop
            insets
        }

        val mainLayout: ScrollView = findViewById(R.id.main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            insets
        }

        initViews()
        setData()
    }

    private fun initViews() {
        mCoverImageView = findViewById(R.id.cover_image_view)
        mTitleTextView = findViewById(R.id.title_text_view)
        mDescTextView = findViewById(R.id.desc_text_view)
        mFavoriteImageView = findViewById(R.id.favorite_image_view)
        mCommentEditText = findViewById(R.id.comment_edit_text)
    }

    private fun setData() {
        intent?.let {
            mMovie = it.getParcelableExtra(Movie::class.java.simpleName)
        }

        mCoverImageView.setImageDrawable(resources.getDrawable(mMovie.mCoverResId))
        mTitleTextView.text = resources.getString(mMovie.mTitleResId)
        mDescTextView.text = resources.getString(mMovie.mDescResId)
        mFavoriteImageView.isSelected = mMovie.mIsFavorite
        mCommentEditText.setText(mMovie.mComment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveComment()
        outState.putParcelable(Movie::class.java.simpleName, mMovie)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mMovie = savedInstanceState.getParcelable(Movie::class.java.simpleName)!!
    }

    fun onFavoriteClick(view: View) {
        view.isSelected = !view.isSelected
        mMovie.mIsFavorite = !mMovie.mIsFavorite
    }

    fun onInvitationClick(view: View) {
        val title = mTitleTextView.text
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invitation, title))
        startActivity(intent)
    }

    override fun onBackPressed() {
        saveComment()

        val data = Intent()
        data.putExtra(Movie::class.java.simpleName, mMovie)
        setResult(Activity.RESULT_OK, data)
        super.onBackPressed()
    }

    private fun saveComment() {
        val comment = mCommentEditText.text.toString()
        mMovie.mComment = comment
    }
}
