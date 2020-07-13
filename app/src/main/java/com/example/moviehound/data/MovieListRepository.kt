package com.example.moviehound.data

import androidx.paging.LivePagedListBuilder
import com.example.moviehound.api.TmdbService
import com.example.moviehound.db.LocalCache
import com.example.moviehound.model.MovieResult

class MovieListRepository(
    private val service: TmdbService,
    private val cache: LocalCache
) {
    fun movies(): MovieResult {
        val dataSourceFactory = cache.getMovies()

        val boundaryCallback = MovieBoundaryCallback(service, cache)

        val networkState = boundaryCallback.networkState

        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .build()

        return MovieResult(data, networkState)
    }

    companion object {
        private const val DATABASE_PAGE_SIZE = 60
    }
}