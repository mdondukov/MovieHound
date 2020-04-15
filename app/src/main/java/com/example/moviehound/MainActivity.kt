package com.example.moviehound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var mTitle0TextView: TextView
    private lateinit var mTitle1TextView: TextView
    private lateinit var mTitle2TextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as MarginLayoutParams
            params.topMargin = insets.systemWindowInsetTop
            insets
        }

        initViews()
        //TODO Call initData()
    }

    //TODO Call saveInstanceState()

    //TODO Call restoreInstanceState()

    private fun initViews() {
        mTitle0TextView = findViewById(R.id.title_0_text_view)
        mTitle1TextView = findViewById(R.id.title_1_text_view)
        mTitle2TextView = findViewById(R.id.title_2_text_view)
    }

    fun onDetailClick(view: View) {
        when ((view.tag as String).toInt()) {
            0 -> {
                changeTitleColor(mTitle0TextView)
                val intent = Intent(this, DetailActivity::class.java)
                startActivity(intent)
            }
            1 -> {
                changeTitleColor(mTitle1TextView)
                //TODO Start Detail Activity
            }
            2 -> {
                changeTitleColor(mTitle2TextView)
                //TODO Start Detail Activity
            }
        }
    }

    private fun changeTitleColor(textView: TextView) {
        textView.setTextColor(resources.getColor(R.color.colorAccent))
    }

    fun onFavoriteClick(view: View) {
        when ((view.tag as String).toInt()) {
            0 -> switchFavoriteStatus(view)
            1 -> switchFavoriteStatus(view)
            2 -> switchFavoriteStatus(view)
        }
    }

    private fun switchFavoriteStatus(view: View) {
        view.isSelected = !view.isSelected
    }
}
