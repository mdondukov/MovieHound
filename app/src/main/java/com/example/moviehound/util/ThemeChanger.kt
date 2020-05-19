package com.example.moviehound.util

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import com.example.moviehound.AppActivity
import com.example.moviehound.R


object ThemeChanger {
    private var currentTheme = 0
    const val THEME_DEFAULT = 0
    const val THEME_PRO = 1

    fun changeToTheme(activity: Activity, theme: Int) {
        currentTheme = theme
        activity.finish()
        activity.startActivity(Intent(activity, activity.javaClass))
    }

    fun onActivityCreateSetTheme(activity: Activity, settings: SharedPreferences) {
        if (settings.contains(AppActivity.APP_CURRENT_THEME)) {
            currentTheme = settings.getInt(
                AppActivity.APP_CURRENT_THEME, 0)
        }

        when (currentTheme) {
            THEME_DEFAULT -> activity.setTheme(
                R.style.Theme_MovieHound
            )
            THEME_PRO -> activity.setTheme(
                R.style.Theme_MovieHound_Pro
            )
            else -> activity.setTheme(R.style.Theme_MovieHound)
        }
    }
}