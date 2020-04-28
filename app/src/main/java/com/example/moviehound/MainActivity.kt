package com.example.moviehound

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var mSettings: SharedPreferences
    private lateinit var mMovieList: ArrayList<Movie>
    private lateinit var mAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        Utils.onActivityCreateSetTheme(this, mSettings)
        setContentView(R.layout.activity_main)
        initData()
        initViews()
    }

    private fun initData() {
        mMovieList = ArrayList()
        mMovieList = Storage.init()
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
            mMovieList
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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val movies: ArrayList<Movie> = savedInstanceState.getParcelableArrayList(MOVIE_LIST)!!
        mAdapter.resetItems(movies)
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
        mAdapter.resetItems(mMovieList)

        Log.d(TAG, "Favorite: ${movie?.mIsFavorite}. Comment: ${movie?.mComment}")
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
        val TAG = MainActivity::class.java.simpleName
        const val MOVIE_LIST = "movies"
        const val REQUEST_CODE_FOR_EDIT = 1
        const val APP_PREFERENCES = "settings"
        const val APP_CURRENT_THEME = "current_theme"
    }
}
