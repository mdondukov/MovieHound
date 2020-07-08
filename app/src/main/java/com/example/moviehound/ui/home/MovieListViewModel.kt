package com.example.moviehound.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.moviehound.api.ServiceGenerator
import com.example.moviehound.api.State
import com.example.moviehound.data.MovieListDataSource
import com.example.moviehound.data.MovieListDataSourceFactory
import com.example.moviehound.model.Movie
import java.util.concurrent.Executors

class MovieListViewModel : ViewModel() {
    private val networkService = ServiceGenerator.networkService
    private val executor = Executors.newFixedThreadPool(5)
    private val dataSourceFactory =
        MovieListDataSourceFactory(
            executor,
            networkService
        )

    val movieList: LiveData<PagedList<Movie>>
    private val pageSize = 20

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setEnablePlaceholders(false)
            .build()

        movieList = LivePagedListBuilder(dataSourceFactory, config)
            .setFetchExecutor(executor)
            .build()
    }

    fun getNetworkState(): LiveData<State> = Transformations.switchMap(
        dataSourceFactory.sourceLiveData,
        MovieListDataSource::state
    )

    fun listIsEmpty(): Boolean {
        return movieList.value?.isEmpty() ?: true
    }

    fun retry() {
        dataSourceFactory.sourceLiveData.value?.retryAllFailed()
    }
}

