package com.example.moviehound

object Storage {
    fun init(): ArrayList<Movie> {
        val movies: ArrayList<Movie> = ArrayList()

        movies.add(
            Movie(
                0,
                R.drawable.img_sw_4,
                R.string.sw_4_title,
                R.string.sw_4_desc,
                false,
                "Hello World!"
            )
        )
        movies.add(
            Movie(
                1,
                R.drawable.img_sw_5,
                R.string.sw_5_title,
                R.string.sw_5_desc,
                true,
                ""
            )
        )
        movies.add(
            Movie(
                2,
                R.drawable.img_sw_6,
                R.string.sw_6_title,
                R.string.sw_6_desc,
                false,
                ""
            )
        )

        return movies
    }
}