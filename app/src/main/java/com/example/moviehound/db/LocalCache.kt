package com.example.moviehound.db

import androidx.paging.DataSource
import com.example.moviehound.model.MovieModel
import java.util.concurrent.Executor

class LocalCache(
    private val movieDao: MovieDao,
    private val ioExecutor: Executor
) {
    fun insert(movies: List<MovieModel>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            movieDao.insert(movies)
            insertFinished()
        }
    }

    fun getMovies(): DataSource.Factory<Int, MovieModel> {
        return movieDao.getMovies()
    }
}