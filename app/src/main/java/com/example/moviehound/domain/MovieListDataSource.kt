package com.example.moviehound.domain

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.moviehound.AppActivity
import com.example.moviehound.api.NetworkService
import com.example.moviehound.api.State
import com.example.moviehound.data.Movie
import com.example.moviehound.data.Movies
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class MovieListDataSource(
    private val retryExecutor: Executor,
    private val networkService: NetworkService
) : PageKeyedDataSource<Int, Movie>() {

    val state = MutableLiveData<State>()
    private var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>
    ) {
        updateState(State.LOADING)
        networkService.getMovies(AppActivity.API_KEY, 1)
            .enqueue(object : Callback<Movies> {
                override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                    if (response.isSuccessful) {
                        val data = response.body()?.movies
                        val items = data?.map { it } ?: emptyList()
                        retry = null
                        updateState(State.DONE)
                        callback.onResult(items, null, 2)
                    } else {
                        retry = {
                            loadInitial(params, callback)
                        }
                        updateState(State.ERROR)
                    }
                }

                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    retry = {
                        loadInitial(params, callback)
                    }
                    updateState(State.ERROR)
                }
            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        updateState(State.LOADING)
        networkService.getMovies(AppActivity.API_KEY, params.key)
            .enqueue(object : Callback<Movies> {
                override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                    if (response.isSuccessful) {
                        val data = response.body()?.movies
                        val items = data?.map { it } ?: emptyList()
                        retry = null
                        callback.onResult(items, params.key + 1)
                        updateState(State.DONE)
                    } else {
                        retry = {
                            loadAfter(params, callback)
                        }
                        updateState(State.ERROR)
                    }
                }

                override fun onFailure(call: Call<Movies>, t: Throwable) {
                    retry = {
                        loadAfter(params, callback)
                    }
                    updateState(State.ERROR)
                }
            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {

    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }
}