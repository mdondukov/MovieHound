package com.example.moviehound.data

import androidx.lifecycle.MutableLiveData
import com.example.moviehound.AppActivity
import com.example.moviehound.api.NetworkState
import com.example.moviehound.api.TmdbService
import com.example.moviehound.model.DetailModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRepository(
    private val service: TmdbService
) {
    val state = MutableLiveData<NetworkState>()

    fun getDetails(movieId: Int, callback: GetMovieCallback) {
        updateState(NetworkState.LOADING)

        service.getDetail(movieId, AppActivity.API_KEY)
            .enqueue(object : Callback<DetailModel> {
                override fun onFailure(call: Call<DetailModel>, t: Throwable) {
                    updateState(NetworkState.error(t.localizedMessage))
                }

                override fun onResponse(call: Call<DetailModel>, response: Response<DetailModel>) {
                    val data = response.body()
                    if (data != null) {
                        val item = DetailModel(
                            data.genres,
                            data.productionCountries,
                            data.runtime,
                            data.budget
                        )
                        callback.onSuccess(item)
                        updateState(NetworkState.DONE)
                    }
                }

            })
    }

    private fun updateState(state: NetworkState) {
        this.state.postValue(state)
    }

    interface GetMovieCallback {
        fun onSuccess(item: DetailModel)
    }
}