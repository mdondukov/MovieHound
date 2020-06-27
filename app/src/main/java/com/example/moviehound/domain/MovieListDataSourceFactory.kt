package com.example.moviehound.domain

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.moviehound.api.NetworkService
import com.example.moviehound.data.Movie
import java.util.concurrent.Executor

class MovieListDataSourceFactory(
    private val executor: Executor,
    private val networkService: NetworkService
) : DataSource.Factory<Int, Movie>() {

    val sourceLiveData = MutableLiveData<MovieListDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val movieListDataSource =
            MovieListDataSource(
                executor,
                networkService
            )
        sourceLiveData.postValue(movieListDataSource)
        return movieListDataSource
    }
}