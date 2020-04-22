package com.example.moviehound

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var mSettings: SharedPreferences
    private lateinit var mMovieList: ArrayList<Movie>
    private lateinit var mTitle0TextView: TextView
    private lateinit var mTitle1TextView: TextView
    private lateinit var mTitle2TextView: TextView
    private lateinit var mFavorite0ImageView: ImageView
    private lateinit var mFavorite1ImageView: ImageView
    private lateinit var mFavorite2ImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        Utils.onActivityCreateSetTheme(this, mSettings)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as MarginLayoutParams
            params.topMargin = insets.systemWindowInsetTop
            insets
        }

        val mainLayout: ScrollView = findViewById(R.id.main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as MarginLayoutParams
            params.rightMargin = insets.systemWindowInsetRight
            insets
        }

        initViews()
        initData()
        setData()
    }

    private fun initViews() {
        mTitle0TextView = findViewById(R.id.title_0_text_view)
        mTitle1TextView = findViewById(R.id.title_1_text_view)
        mTitle2TextView = findViewById(R.id.title_2_text_view)
        mFavorite0ImageView = findViewById(R.id.favorite_0_image_view)
        mFavorite1ImageView = findViewById(R.id.favorite_1_image_view)
        mFavorite2ImageView = findViewById(R.id.favorite_2_image_view)
    }

    private fun initData() {
        mMovieList = ArrayList()
        mMovieList = Storage.init()
    }

    private fun setData() {
        mFavorite0ImageView.isSelected = mMovieList[0].mIsFavorite
        mFavorite1ImageView.isSelected = mMovieList[1].mIsFavorite
        mFavorite2ImageView.isSelected = mMovieList[2].mIsFavorite
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                switchTheme()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun switchTheme() {
        if (mSettings.contains(APP_CURRENT_THEME)) {
            when (mSettings.getInt(APP_CURRENT_THEME, 0)) {
                Utils.THEME_PRO -> {
                    Utils.changeToTheme(this, Utils.THEME_DEFAULT)
                    saveCurrentTheme(Utils.THEME_DEFAULT)
                }
                Utils.THEME_DEFAULT -> {
                    Utils.changeToTheme(this, Utils.THEME_PRO)
                    saveCurrentTheme(Utils.THEME_PRO)
                }
            }
        } else {
            Utils.changeToTheme(this, Utils.THEME_PRO)
            saveCurrentTheme(Utils.THEME_PRO)
        }
    }

    private fun saveCurrentTheme(theme: Int) {
        val editor: SharedPreferences.Editor = mSettings.edit()
        editor.putInt(APP_CURRENT_THEME, theme)
        editor.apply()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(::mTitle0TextView.name, mTitle0TextView.currentTextColor)
        outState.putInt(::mTitle1TextView.name, mTitle1TextView.currentTextColor)
        outState.putInt(::mTitle2TextView.name, mTitle2TextView.currentTextColor)

        outState.putParcelableArrayList(MOVIE_LIST, mMovieList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        mTitle0TextView.setTextColor(savedInstanceState.getInt(::mTitle0TextView.name))
        mTitle1TextView.setTextColor(savedInstanceState.getInt(::mTitle1TextView.name))
        mTitle2TextView.setTextColor(savedInstanceState.getInt(::mTitle2TextView.name))

        val movies: ArrayList<Movie> = savedInstanceState.getParcelableArrayList(MOVIE_LIST)!!
        mMovieList = movies
        setData()
    }

    fun onDetailClick(view: View) {
        val tag: Int = (view.tag as String).toInt()
        val movie: Movie = mMovieList[tag]
        when (tag) {
            0 -> {
                setTitleAccentColor(mTitle0TextView)
                startDetailActivity(movie)
            }
            1 -> {
                setTitleAccentColor(mTitle1TextView)
                startDetailActivity(movie)
            }
            2 -> {
                setTitleAccentColor(mTitle2TextView)
                startDetailActivity(movie)
            }
        }
    }

    private fun setTitleAccentColor(textView: TextView) {
        textView.setTextColor(resources.getColor(R.color.colorAccent))
    }

    private fun startDetailActivity(movie: Movie) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(Movie::class.java.simpleName, movie)
        startActivityForResult(intent, REQUEST_CODE_FOR_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FOR_EDIT && resultCode == Activity.RESULT_OK) {
            updateMovieList(data?.getParcelableExtra(Movie::class.java.simpleName))
        }
    }

    private fun updateMovieList(movie: Movie?) {
        val index: Int = mMovieList.indexOf(movie)
        movie?.let { mMovieList.set(index, it) }
        setData()

        Log.d(TAG, "Favorite: ${movie?.mIsFavorite}. Comment: ${movie?.mComment}")
    }

    fun onFavoriteClick(view: View) {
        val tag: Int = (view.tag as String).toInt()
        val movie: Movie = mMovieList[tag]
        when (tag) {
            0 -> switchFavoriteStatus(view, movie)
            1 -> switchFavoriteStatus(view, movie)
            2 -> switchFavoriteStatus(view, movie)
        }
    }

    private fun switchFavoriteStatus(view: View, movie: Movie) {
        view.isSelected = !view.isSelected
        movie.mIsFavorite = !movie.mIsFavorite
    }

    override fun onBackPressed() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_exit)
        dialog.window?.setBackgroundDrawable(this.getDrawable(R.drawable.dialog_rounded_background))
        dialog.findViewById<Button>(R.id.exit_positive_button).setOnClickListener {
            super.onBackPressed()
        }
        dialog.findViewById<Button>(R.id.exit_negative_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
        const val MOVIE_LIST = "movies"
        const val REQUEST_CODE_FOR_EDIT = 1
        const val APP_PREFERENCES = "settings"
        const val APP_CURRENT_THEME = "current_theme"
    }
}
