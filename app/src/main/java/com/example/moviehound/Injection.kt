package com.example.moviehound

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.moviehound.api.TmdbService
import com.example.moviehound.data.DetailRepository
import com.example.moviehound.data.MovieRepository
import com.example.moviehound.db.LocalCache
import com.example.moviehound.db.MovieRoomDatabase
import com.example.moviehound.ui.detail.DetailViewModelFactory
import com.example.moviehound.ui.global.ShareViewModelFactory
import com.example.moviehound.ui.home.MovieListViewModelFactory
import java.util.concurrent.Executors

object Injection {
    private fun provideMovieListCache(context: Context): LocalCache {
        val database = MovieRoomDatabase.getInstance(context)
        return LocalCache(database.movieDao(), Executors.newSingleThreadExecutor())
    }

    private fun provideMovieRepository(context: Context): MovieRepository {
        return MovieRepository(TmdbService.create(), provideMovieListCache(context))
    }

    fun provideMovieViewModelFactory(context: Context): ViewModelProvider.Factory {
        return MovieListViewModelFactory(provideMovieRepository(context))
    }

    fun provideShareViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ShareViewModelFactory(provideMovieRepository(context))
    }

    private fun provideDetailRepository(): DetailRepository {
        return DetailRepository(TmdbService.create())
    }

    fun provideDetailViewModelFactory(): ViewModelProvider.Factory {
        return DetailViewModelFactory(provideDetailRepository())
    }
}