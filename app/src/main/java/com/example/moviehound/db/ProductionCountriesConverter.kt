package com.example.moviehound.db

import androidx.room.TypeConverter
import com.example.moviehound.model.ProductionCountry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductionCountriesConverter {
    @TypeConverter
    fun fromImagesJson(stat: List<ProductionCountry>): String {
        return Gson().toJson(stat)
    }

    @TypeConverter
    fun toImagesList(jsonProductionCountries: String): List<ProductionCountry> {
        val notesType = object : TypeToken<List<ProductionCountry>>() {}.type
        return Gson().fromJson<List<ProductionCountry>>(jsonProductionCountries, notesType)
    }
}