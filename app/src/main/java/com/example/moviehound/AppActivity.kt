package com.example.moviehound

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.moviehound.data.Movie
import com.example.moviehound.data.Storage
import com.example.moviehound.ui.detail.DetailFragment
import com.example.moviehound.ui.favorites.FavoriteListFragment
import com.example.moviehound.ui.global.OnMovieListClickListener
import com.example.moviehound.ui.home.MovieListFragment
import com.example.moviehound.ui.search.SearchFragment
import com.example.moviehound.util.ThemeChanger
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppActivity : AppCompatActivity(),
    OnMovieListClickListener,
    DetailFragment.OnMovieChanged {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var movieList: ArrayList<Movie>
    private lateinit var favoriteList: ArrayList<Movie>
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        ThemeChanger.onActivityCreateSetTheme(this, sharedPreferences)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        initData(savedInstanceState)
        initBottomNavigation()
        setStartFragment()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.fragments.last() !is DetailFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                if (sharedPreferences.contains(APP_CURRENT_THEME)) {
                    when (sharedPreferences.getInt(APP_CURRENT_THEME, 0)) {
                        ThemeChanger.THEME_PRO -> window.statusBarColor =
                            resources.getColor(R.color.colorPrimaryDarkPro)
                        ThemeChanger.THEME_DEFAULT -> window.statusBarColor =
                            resources.getColor(R.color.colorPrimaryDark)
                    }
                }
            }
        }
    }

    private fun initData(savedInstanceState: Bundle?) {
        movieList = ArrayList()
        movieList = Storage.init()
        favoriteList = ArrayList()

        if (savedInstanceState != null) {
            movieList =
                savedInstanceState.getParcelableArrayList<Movie>(MOVIE_LIST) as ArrayList<Movie>
            favoriteList =
                savedInstanceState.getParcelableArrayList<Movie>(FAVORITE_LIST) as ArrayList<Movie>
        }
    }

    private fun initBottomNavigation() {
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener {
            val currentFragment = supportFragmentManager.fragments.last()
            when (it.itemId) {
                R.id.navigation_home -> {
                    if (currentFragment !is MovieListFragment)
                        supportFragmentManager.popBackStack(
                            null,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_favorite -> {
                    if (currentFragment !is FavoriteListFragment)
                        doFragment(FavoriteListFragment.newInstance(favoriteList))
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_search -> {
                    if (currentFragment !is SearchFragment)
                        doFragment(SearchFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    private fun doFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setStartFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (supportFragmentManager.fragments.last() is DetailFragment) {
                supportFragmentManager.fragments.last()
            }
        } else {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, MovieListFragment.newInstance(movieList, favoriteList))
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(MOVIE_LIST, movieList)
        outState.putParcelableArrayList(FAVORITE_LIST, favoriteList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_theme -> {
                switchTheme()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun switchTheme() {
        if (sharedPreferences.contains(APP_CURRENT_THEME)) {
            when (sharedPreferences.getInt(APP_CURRENT_THEME, 0)) {
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
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(APP_CURRENT_THEME, theme)
        editor.apply()
    }

    override fun onMovieClick(item: Movie) {
        doFragment(DetailFragment.newInstance(item, favoriteList))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun setMovieResult(item: Movie, favorites: ArrayList<Movie>) {
        favorites.let { favoriteList = favorites }
        val index: Int = movieList.indexOf(item)
        item.let { movieList.set(index, it) }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.last() is DetailFragment) {
            supportFragmentManager.popBackStack()
        } else showExitDialog()
    }

    private fun showExitDialog() {
        val dialog = Dialog(this)
        dialog.apply {
            setContentView(R.layout.dialog_exit)
            window?.setBackgroundDrawable(context.getDrawable(R.drawable.dialog_rounded_bg))
            findViewById<Button>(R.id.exit_positive_btn).setOnClickListener {
                dialog.dismiss()
                super.onBackPressed()
            }
            findViewById<Button>(R.id.exit_negative_btn).setOnClickListener {
                dialog.dismiss()
            }
            show()
        }
    }

    companion object {
        const val MOVIE_LIST = "movies"
        const val FAVORITE_LIST = "favorites"
        const val APP_PREFERENCES = "settings"
        const val APP_CURRENT_THEME = "current_theme"
    }
}
