package com.example.moviehound

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviehound.data.Movie
import com.example.moviehound.data.Storage
import com.example.moviehound.ui.adapters.MovieAdapter
import com.example.moviehound.ui.itemdecorations.MovieItemDecoration

class MainActivity : AppCompatActivity() {
    private lateinit var mSettings: SharedPreferences
    private lateinit var mMovieList: ArrayList<Movie>
    private lateinit var mFavoriteList: ArrayList<Movie>
    private lateinit var mAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        ThemeChanger.onActivityCreateSetTheme(this, mSettings)
        setContentView(R.layout.activity_main)
        initData()
        initViews()
    }

    private fun initData() {
        mMovieList = ArrayList()
        mMovieList = Storage.init()
        mFavoriteList = ArrayList()
    }

    private fun initViews() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val mainLayout: FrameLayout = findViewById(R.id.main_layout)
        setInsets(toolbar)
        setInsets(mainLayout)

        val recycler = findViewById<RecyclerView>(R.id.movie_list)
        val gridColumnCount: Int = resources.getInteger(R.integer.grid_column_count)
        val layoutManager = GridLayoutManager(this, gridColumnCount)
        recycler.layoutManager = layoutManager

        mAdapter = MovieAdapter(
            LayoutInflater.from(this),
            mMovieList,
            mFavoriteList
        ) { item -> startDetailActivity(item) }

        recycler.adapter = mAdapter
        recycler.addItemDecoration(
            MovieItemDecoration(
                resources.getDimension(R.dimen.margin_sm).toInt()
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(MOVIE_LIST, mAdapter.getItems())
        outState.putParcelableArrayList(FAVORITE_LIST, mFavoriteList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val movies: ArrayList<Movie> = savedInstanceState.getParcelableArrayList(MOVIE_LIST)!!
        mFavoriteList = savedInstanceState.getParcelableArrayList(FAVORITE_LIST)!!
        mAdapter.resetItems(movies, mFavoriteList)
    }

    private fun startDetailActivity(movie: Movie) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(Movie::class.java.simpleName, movie)
        intent.putParcelableArrayListExtra(FAVORITE_LIST, mFavoriteList)
        startActivityForResult(intent, REQUEST_CODE_FOR_MOVIE_EDIT)
    }

    private fun startFavoriteActivity() {
        val intent = Intent(this, FavoriteActivity::class.java)
        intent.putParcelableArrayListExtra(FAVORITE_LIST, mFavoriteList)
        intent.putParcelableArrayListExtra(MOVIE_LIST, mMovieList)
        startActivityForResult(intent, REQUEST_CODE_FOR_FAVORITES_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FOR_MOVIE_EDIT && resultCode == Activity.RESULT_OK) {
            val movie: Movie? = data?.getParcelableExtra(
                Movie::class.java.simpleName)
            val favorites: ArrayList<Movie>? = data?.getParcelableArrayListExtra(FAVORITE_LIST)
            updateAllLists(movie, favorites)

        } else if (requestCode == REQUEST_CODE_FOR_FAVORITES_EDIT && resultCode == Activity.RESULT_OK) {
            data?.let {
                mFavoriteList = it.getParcelableArrayListExtra(FAVORITE_LIST)!!
                mMovieList = it.getParcelableArrayListExtra(MOVIE_LIST)!!
            }
            mAdapter.resetItems(mMovieList, mFavoriteList)
        }
    }

    private fun updateAllLists(movie: Movie?, favorites: ArrayList<Movie>?) {
        favorites?.let { mFavoriteList = favorites }
        val index: Int = mMovieList.indexOf(movie)
        movie?.let { mMovieList.set(index, it) }
        mAdapter.resetItems(mMovieList, mFavoriteList)
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
            R.id.action_favorites -> {
                startFavoriteActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun switchTheme() {
        if (mSettings.contains(APP_CURRENT_THEME)) {
            when (mSettings.getInt(APP_CURRENT_THEME, 0)) {
                ThemeChanger.THEME_PRO -> {
                    ThemeChanger.changeToTheme(this, ThemeChanger.THEME_DEFAULT)
                    saveCurrentTheme(ThemeChanger.THEME_DEFAULT)
                }
                ThemeChanger.THEME_DEFAULT -> {
                    ThemeChanger.changeToTheme(this, ThemeChanger.THEME_PRO)
                    saveCurrentTheme(ThemeChanger.THEME_PRO)
                }
            }
        } else {
            ThemeChanger.changeToTheme(this, ThemeChanger.THEME_PRO)
            saveCurrentTheme(ThemeChanger.THEME_PRO)
        }
    }

    private fun saveCurrentTheme(theme: Int) {
        val editor: SharedPreferences.Editor = mSettings.edit()
        editor.putInt(APP_CURRENT_THEME, theme)
        editor.apply()
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

    private fun setInsets(toolbar: Toolbar) {
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as MarginLayoutParams
            params.topMargin = insets.systemWindowInsetTop
            insets
        }
    }

    private fun setInsets(frameLayout: FrameLayout) {
        ViewCompat.setOnApplyWindowInsetsListener(frameLayout) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as MarginLayoutParams
            params.rightMargin = insets.systemWindowInsetRight
            insets
        }
    }

    companion object {
        const val MOVIE_LIST = "movies"
        const val FAVORITE_LIST = "favorites"
        const val REQUEST_CODE_FOR_MOVIE_EDIT = 1
        const val REQUEST_CODE_FOR_FAVORITES_EDIT = 2
        const val APP_PREFERENCES = "settings"
        const val APP_CURRENT_THEME = "current_theme"
    }
}
