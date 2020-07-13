package com.example.moviehound

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.moviehound.api.TmdbService
import com.example.moviehound.data.DetailRepository
import com.example.moviehound.data.MovieListRepository
import com.example.moviehound.db.LocalCache
import com.example.moviehound.db.MovieRoomDatabase
import com.example.moviehound.ui.detail.DetailViewModelFactory
import com.example.moviehound.ui.home.MovieListViewModelFactory
import java.util.concurrent.Executors

object Injection {
    private fun provideMovieListCache(context: Context): LocalCache {
        val database = MovieRoomDatabase.getInstance(context)
        return LocalCache(database.movieDao(), Executors.newSingleThreadExecutor())
    }

    private fun provideMovieListRepository(context: Context): MovieListRepository {
        return MovieListRepository(TmdbService.create(), provideMovieListCache(context))
    }

    fun provideMovieViewModelFactory(context: Context): ViewModelProvider.Factory {
        return MovieListViewModelFactory(provideMovieListRepository(context))
    }

    private fun provideDetailRepository(): DetailRepository {
        return DetailRepository(TmdbService.create())
    }

    fun provideDetailViewModelFactory(): ViewModelProvider.Factory {
        return DetailViewModelFactory(provideDetailRepository())
    }
}