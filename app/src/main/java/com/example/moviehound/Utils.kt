package com.example.moviehound

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences


object Utils {
    private var sCurrentTheme = 0
    const val THEME_DEFAULT = 0
    const val THEME_PRO = 1

    fun changeToTheme(activity: Activity, theme: Int) {
        sCurrentTheme = theme
        activity.finish()
        activity.startActivity(Intent(activity, activity.javaClass))
    }

    fun onActivityCreateSetTheme(activity: Activity, settings: SharedPreferences) {
        if (settings.contains(MainActivity.APP_CURRENT_THEME)) {
            sCurrentTheme = settings.getInt(MainActivity.APP_CURRENT_THEME, 0)
        }

        when (sCurrentTheme) {
            THEME_DEFAULT -> activity.setTheme(R.style.Theme_MovieHound)
            THEME_PRO -> activity.setTheme(R.style.Theme_MovieHound_Pro)
            else -> activity.setTheme(R.style.Theme_MovieHound)
        }
    }
}