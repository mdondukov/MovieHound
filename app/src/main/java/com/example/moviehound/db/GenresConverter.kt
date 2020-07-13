package com.example.moviehound.db

import androidx.room.TypeConverter
import com.example.moviehound.model.Genre
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GenresConverter {
    @TypeConverter
    fun fromGenreListToJson(genres: List<Genre>?): String {
        var data = ""
        if (genres != null) {
            data = Gson().toJson(genres)
        }
        return data
    }

    @TypeConverter
    fun fromJsonToGenreList(jsonGenres: String): List<Genre> {
        val notesType = object : TypeToken<List<Genre>>() {}.type
        return Gson().fromJson<List<Genre>>(jsonGenres, notesType)
    }
}