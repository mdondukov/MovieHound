package com.example.moviehound

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.moviehound.data.Movie
import com.example.moviehound.data.Storage
import com.example.moviehound.ui.detail.MovieFragment
import com.example.moviehound.ui.favorites.FavoriteListFragment
import com.example.moviehound.ui.global.OnMovieListClickListener
import com.example.moviehound.ui.home.MovieListFragment
import com.example.moviehound.util.ThemeChanger
import com.example.moviehound.util.doOnApplyWindowInsets
import com.google.android.material.navigation.NavigationView

class AppActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    OnMovieListClickListener,
    MovieFragment.OnMovieChanged {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var movieList: ArrayList<Movie>
    private lateinit var favoriteList: ArrayList<Movie>
    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        ThemeChanger.onActivityCreateSetTheme(this, sharedPreferences)

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                decorView.systemUiVisibility = decorView.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }

            navigationBarColor =
                ContextCompat.getColor(context, R.color.white)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        initData(savedInstanceState)
        initViews()
        setStartFragment()

        if (savedInstanceState != null) {
            supportActionBar?.title =
                savedInstanceState.getString(TOOLBAR_TITLE, resources.getString(R.string.app_name))
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.fragments.last()
            if (currentFragment is MovieFragment)
                setToolbarBehavior(true)
            else {
                setToolbarBehavior(false)

                when (currentFragment) {
                    is MovieListFragment -> {
                        navView.setCheckedItem(R.id.nav_home)
                        supportActionBar?.setTitle(R.string.home)
                    }
                    is FavoriteListFragment -> {
                        navView.setCheckedItem(R.id.nav_favorite)
                        supportActionBar?.setTitle(R.string.favorite)
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

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        toolbar.doOnApplyWindowInsets { view, insets, margin ->
            val params = view.layoutParams as MarginLayoutParams
            params.topMargin = margin.top + insets.systemWindowInsetTop
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navView.doOnApplyWindowInsets { view, insets, margin ->
            val params = view.layoutParams as MarginLayoutParams
            params.bottomMargin = margin.bottom + insets.systemWindowInsetBottom
            insets
        }
        initNavDrawer()
    }

    private fun initNavDrawer() {
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    private fun setStartFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (supportFragmentManager.fragments.last() is MovieFragment) {
                supportFragmentManager.fragments.last()
                setToolbarBehavior(true)
            }
        } else {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.container,
                    MovieListFragment.newInstance(movieList, favoriteList)
                )
                .commit()

            navView.setCheckedItem(R.id.nav_home)
            supportActionBar?.setTitle(R.string.home)
        }
    }

    private fun setToolbarBehavior(isBackButtonEnabled: Boolean) {
        if (isBackButtonEnabled) {
            supportActionBar?.setDisplayHomeAsUpEnabled(isBackButtonEnabled)
            supportActionBar?.setTitle(R.string.about_movie)
            toolbar.setNavigationOnClickListener {
                super.onBackPressed()
            }
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(isBackButtonEnabled)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            toolbar.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(MOVIE_LIST, movieList)
        outState.putParcelableArrayList(FAVORITE_LIST, favoriteList)
        outState.putString(TOOLBAR_TITLE, toolbar.title.toString())
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
                if (supportFragmentManager.fragments.last() is MovieListFragment) {
                    supportFragmentManager.fragments.last()
                } else {
                    supportFragmentManager.popBackStack(
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
            R.id.nav_favorite -> {
                startFragment(FavoriteListFragment.newInstance(favoriteList))
            }
            R.id.nav_change_theme -> {
                switchTheme()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
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
        startFragment(MovieFragment.newInstance(item, favoriteList))
    }

    override fun setMovieResult(item: Movie, favorites: ArrayList<Movie>) {
        favorites.let { favoriteList = favorites }
        val index: Int = movieList.indexOf(item)
        item.let { movieList.set(index, it) }
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
        dialog.window?.setBackgroundDrawable(this.getDrawable(R.drawable.dialog_rounded_bg))
        dialog.findViewById<Button>(R.id.exit_positive_btn).setOnClickListener {
            super.onBackPressed()
        }
        dialog.findViewById<Button>(R.id.exit_negative_btn).setOnClickListener {
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
