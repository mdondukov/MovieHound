package com.example.moviehound.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.example.moviehound.model.MovieModel
import java.util.concurrent.Executor

class LocalCache(
    private val movieDao: MovieDao,
    private val ioExecutor: Executor
) {
    fun insertAll(movies: List<MovieModel>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            movieDao.insert(movies)
            insertFinished()
        }
    }

    fun updateMovie(movie: MovieModel) {
        ioExecutor.execute {
            movieDao.update(movie)
        }
    }

    fun getMovies(): DataSource.Factory<Int, MovieModel> {
        return movieDao.getMovies()
    }

    fun getFavorites(): LiveData<List<MovieModel>> {
        return  movieDao.getFavorites()
    }
}