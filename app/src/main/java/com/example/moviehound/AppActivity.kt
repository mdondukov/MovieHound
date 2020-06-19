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
import com.example.moviehound.ui.detail.DetailFragment
import com.example.moviehound.ui.favorites.FavoriteListFragment
import com.example.moviehound.ui.global.OnMovieListClickListener
import com.example.moviehound.ui.home.MovieListFragment
import com.example.moviehound.ui.search.SearchFragment
import com.example.moviehound.util.ThemeChanger
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppActivity : AppCompatActivity(),
    OnMovieListClickListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        ThemeChanger.onActivityCreateSetTheme(this, sharedPreferences)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        initBottomNavigation()
        setStartFragment(savedInstanceState)

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
                        doFragment(FavoriteListFragment())
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

    private fun setStartFragment(savedInstanceState: Bundle?) {
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (supportFragmentManager.fragments.last() is DetailFragment) {
                supportFragmentManager.fragments.last()
            }
        } else {
            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, MovieListFragment())
                    .commit()
            }
        }
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

    override fun onMovieClick() {
        doFragment(DetailFragment())
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        const val APP_PREFERENCES = "settings"
        const val APP_CURRENT_THEME = "current_theme"
        const val API_KEY = "2b2917453d5b58d5a9796598046553b1"
    }
}
