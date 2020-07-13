package com.example.moviehound.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviehound.api.NetworkState
import com.example.moviehound.data.DetailRepository
import com.example.moviehound.model.DetailModel

class DetailViewModel(private val repository: DetailRepository) : ViewModel() {
    private val movieDetailLiveData = MutableLiveData<DetailModel>()

    val movieDetail: LiveData<DetailModel>
        get() = movieDetailLiveData

    fun getDetails(movieId: Int) {
        repository.getDetails(movieId, object : DetailRepository.GetMovieCallback {
            override fun onSuccess(item: DetailModel) {
                movieDetailLiveData.postValue(item)
            }
        })
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return repository.state
    }
}