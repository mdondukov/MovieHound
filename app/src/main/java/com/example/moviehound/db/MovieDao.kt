package com.example.moviehound.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.example.moviehound.model.MovieModel

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY popularity DESC, voteCount Desc")
    fun getMovies(): DataSource.Factory<Int, MovieModel>

    @Query("SELECT * FROM movies WHERE is_favorite = 1")
    fun getFavorites(): LiveData<List<MovieModel>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovie(id: Int): LiveData<MovieModel>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(movie: MovieModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<MovieModel>)

    @Query("DELETE FROM movies")
    fun delete()
}