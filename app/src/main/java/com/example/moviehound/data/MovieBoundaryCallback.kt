package com.example.moviehound.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.moviehound.api.NetworkState
import com.example.moviehound.api.TmdbService
import com.example.moviehound.api.getMovies
import com.example.moviehound.db.LocalCache
import com.example.moviehound.model.MovieModel

class MovieBoundaryCallback(
    private val service: TmdbService,
    private val cache: LocalCache
) : PagedList.BoundaryCallback<MovieModel>() {
    private val networkStateLiveData = MutableLiveData<NetworkState>()

    val networkState: LiveData<NetworkState>
        get() = networkStateLiveData

    private var lastRequestedPage = 1
    private var isRequestInProgress = false

    override fun onZeroItemsLoaded() {
        requestAndSaveData()
    }

    override fun onItemAtEndLoaded(itemAtEnd: MovieModel) {
        requestAndSaveData()
    }

    private fun requestAndSaveData() {
        if (isRequestInProgress) return

        updateState(NetworkState.LOADING)
        isRequestInProgress = true

        getMovies(service, lastRequestedPage, { movieResponse ->
            val data = movieResponse.movies
            val items = data?.map { it } ?: emptyList()
            cache.insert(items) {
                lastRequestedPage++
                updateState(NetworkState.DONE)
                isRequestInProgress = false
            }
        }, { error ->
            updateState(NetworkState.error(error))
            isRequestInProgress = false
        })
    }

    private fun updateState(state: NetworkState) {
        this.networkStateLiveData.postValue(state)
    }
}