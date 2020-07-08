package com.example.moviehound.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.moviehound.model.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getMovies(): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<Movie>)

    @Query("DELETE FROM movies")
    fun deleteAll()

    @Query("SELECT * FROM movies WHERE id=:id")
    fun getMovie(id: Int): LiveData<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Movie)

    @Delete
    fun delete(item: Movie)
}