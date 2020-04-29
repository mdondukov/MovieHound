package com.example.moviehound

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteActivity : AppCompatActivity() {
    private lateinit var mSettings: SharedPreferences
    private lateinit var mFavoriteList: ArrayList<Movie>
    private lateinit var mMovieList: ArrayList<Movie>
    private lateinit var mAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        mSettings = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE)
        Utils.onActivityCreateSetTheme(this, mSettings)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        initData()
        initViews()
    }

    private fun initData() {
        mFavoriteList = ArrayList()
        intent?.let {
            mFavoriteList = it.getParcelableArrayListExtra(MainActivity.FAVORITE_LIST)!!
            mMovieList = it.getParcelableArrayListExtra(MainActivity.MOVIE_LIST)!!
        }
    }

    private fun initViews() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.favorite)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val mainLayout: FrameLayout = findViewById(R.id.main_layout)
        setInsets(toolbar)
        setInsets(mainLayout)

        val recycler = findViewById<RecyclerView>(R.id.movie_list)
        val gridColumnCount: Int = resources.getInteger(R.integer.grid_column_count)
        val layoutManager = GridLayoutManager(this, gridColumnCount)
        recycler.layoutManager = layoutManager

        mAdapter = FavoriteAdapter(
            LayoutInflater.from(this),
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
        outState.putParcelableArrayList(MainActivity.FAVORITE_LIST, mAdapter.getItems())
        outState.putParcelableArrayList(MainActivity.MOVIE_LIST, mMovieList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val favorites: ArrayList<Movie> = savedInstanceState.getParcelableArrayList(MainActivity.FAVORITE_LIST)!!
        mMovieList = savedInstanceState.getParcelableArrayList(MainActivity.MOVIE_LIST)!!
        mAdapter.resetItems(favorites)
    }

    private fun startDetailActivity(item: Movie) {
        val movie: Movie = mMovieList[mMovieList.indexOf(item)]
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(Movie::class.java.simpleName, movie)
        intent.putParcelableArrayListExtra(MainActivity.FAVORITE_LIST, mFavoriteList)
        startActivityForResult(intent, MainActivity.REQUEST_CODE_FOR_MOVIE_EDIT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.REQUEST_CODE_FOR_MOVIE_EDIT && resultCode == Activity.RESULT_OK) {
            val movie: Movie? = data?.getParcelableExtra(Movie::class.java.simpleName)
            val favorites: ArrayList<Movie>? = data?.getParcelableArrayListExtra(MainActivity.FAVORITE_LIST)
            updateAllLists(movie, favorites)
        }
    }

    private fun updateAllLists(movie: Movie?, favorites: ArrayList<Movie>?) {
        favorites?.let { mFavoriteList = favorites }
        val index: Int = mMovieList.indexOf(movie)
        movie?.let { mMovieList.set(index, it) }
        mAdapter.resetItems(mFavoriteList)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setInsets(toolbar: Toolbar) {
        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = insets.systemWindowInsetTop
            insets
        }
    }

    private fun setInsets(frameLayout: FrameLayout) {
        ViewCompat.setOnApplyWindowInsetsListener(frameLayout) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.rightMargin = insets.systemWindowInsetRight
            insets
        }
    }

    override fun onBackPressed() {
        val data = Intent()
        data.putParcelableArrayListExtra(MainActivity.FAVORITE_LIST, mFavoriteList)
        data.putParcelableArrayListExtra(MainActivity.MOVIE_LIST, mMovieList)
        setResult(Activity.RESULT_OK, data)
        super.onBackPressed()
    }
}
