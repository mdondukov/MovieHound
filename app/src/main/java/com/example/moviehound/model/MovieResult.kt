package com.example.moviehound.model

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.example.moviehound.api.NetworkState

data class MovieResult(
    val data: LiveData<PagedList<MovieModel>>,
    val networkState: LiveData<NetworkState>
)