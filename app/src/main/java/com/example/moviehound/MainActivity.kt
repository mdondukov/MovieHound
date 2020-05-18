package com.example.moviehound

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.moviehound.data.Movie
import com.example.moviehound.data.Storage
import com.example.moviehound.ui.fragments.DetailFragment
import com.example.moviehound.ui.fragments.FavoriteFragment
import com.example.moviehound.ui.fragments.HomeFragment
import com.example.moviehound.ui.fragments.SettingsFragment
import com.example.moviehound.util.ThemeChanger
import com.example.moviehound.util.doOnApplyWindowInsets
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    OnMovieListClickListener,
    DetailFragment.OnMovieChanged {
    private lateinit var mSettings: SharedPreferences
    private lateinit var mMovieList: ArrayList<Movie>
    private lateinit var mFavoriteList: ArrayList<Movie>
    private lateinit var mToolbar: Toolbar
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mToggle: ActionBarDrawerToggle
    private lateinit var mNavView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        ThemeChanger.onActivityCreateSetTheme(this, mSettings)
        setContentView(R.layout.activity_main)
        initData(savedInstanceState)
        initViews()
        setStartFragment()

        if (savedInstanceState != null) {
            supportActionBar?.title =
                savedInstanceState.getString(TOOLBAR_TITLE, resources.getString(R.string.app_name))
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.fragments.last()
            if (currentFragment is DetailFragment)
                setToolbarBehavior(true)
            else {
                setToolbarBehavior(false)

                when (currentFragment) {
                    is HomeFragment -> {
                        mNavView.setCheckedItem(R.id.nav_home)
                        supportActionBar?.setTitle(R.string.home)
                    }
                    is FavoriteFragment -> {
                        mNavView.setCheckedItem(R.id.nav_favorite)
                        supportActionBar?.setTitle(R.string.favorite)
                    }
                    is SettingsFragment -> {
                        mNavView.setCheckedItem(R.id.nav_settings)
                        supportActionBar?.setTitle(R.string.settings)
                    }
                }
            }
        }
    }

    private fun initData(savedInstanceState: Bundle?) {
        mMovieList = ArrayList()
        mMovieList = Storage.init()
        mFavoriteList = ArrayList()

        if (savedInstanceState != null) {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST)!!
            mFavoriteList = savedInstanceState.getParcelableArrayList(FAVORITE_LIST)!!
        }
    }

    private fun initViews() {
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        mToolbar.doOnApplyWindowInsets { view, insets, margin ->
            val params = view.layoutParams as MarginLayoutParams
            params.topMargin = margin.top + insets.systemWindowInsetTop
            insets
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)
        mNavView = findViewById(R.id.nav_view)
        initNavDrawer()
    }

    private fun initNavDrawer() {
        mToggle = ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            mToolbar,
            R.string.open, R.string.close
        )
        mDrawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        mNavView.setNavigationItemSelectedListener(this)
    }

    private fun setStartFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (supportFragmentManager.fragments.last() is DetailFragment) {
                supportFragmentManager.fragments.last()
                setToolbarBehavior(true)
            }
        } else {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.container,
                    HomeFragment.newInstance(mMovieList, mFavoriteList)
                )
                .commit()

            mNavView.setCheckedItem(R.id.nav_home)
            supportActionBar?.setTitle(R.string.home)
        }
    }

    private fun setToolbarBehavior(isBackButtonEnabled: Boolean) {
        if (isBackButtonEnabled) {
            supportActionBar?.setDisplayHomeAsUpEnabled(isBackButtonEnabled)
            supportActionBar?.setTitle(R.string.about_movie)
            mToolbar.setNavigationOnClickListener {
                super.onBackPressed()
            }
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(isBackButtonEnabled)
            mDrawerLayout.addDrawerListener(mToggle)
            mToggle.syncState()
            mToolbar.setNavigationOnClickListener {
                mDrawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(MOVIE_LIST, mMovieList)
        outState.putParcelableArrayList(FAVORITE_LIST, mFavoriteList)
        outState.putString(TOOLBAR_TITLE, mToolbar.title.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                if (supportFragmentManager.fragments.last() is HomeFragment) {
                    supportFragmentManager.fragments.last()
                } else {
                    supportFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
            R.id.nav_favorite -> {
                startFragment(FavoriteFragment.newInstance(mFavoriteList))
            }
            R.id.nav_settings -> {
                startFragment(SettingsFragment())
            }
            R.id.nav_change_theme -> {
                switchTheme()
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                fragment,
                fragment::class.java.simpleName
            )
            .addToBackStack(null)
            .commit()
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

    override fun onMovieClick(movie: Movie) {
        startFragment(DetailFragment.newInstance(movie, mFavoriteList))
    }

    override fun setMovieResult(movie: Movie, favorites: ArrayList<Movie>) {
        favorites.let { mFavoriteList = favorites }
        val index: Int = mMovieList.indexOf(movie)
        movie.let { mMovieList.set(index, it) }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            getExitDialog()
        }
    }

    private fun getExitDialog() {
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
        const val MOVIE_LIST = "movies"
        const val FAVORITE_LIST = "favorites"
        const val TOOLBAR_TITLE = "toolbar_title"
        const val APP_PREFERENCES = "settings"
        const val APP_CURRENT_THEME = "current_theme"
    }
}
