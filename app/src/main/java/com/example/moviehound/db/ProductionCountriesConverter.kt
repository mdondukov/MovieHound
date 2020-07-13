package com.example.moviehound.db

import androidx.room.TypeConverter
import com.example.moviehound.model.ProductionCountry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductionCountriesConverter {
    @TypeConverter
    fun fromImagesJson(countries: List<ProductionCountry>?): String {
        var data = ""
        if (countries != null) {
            data = Gson().toJson(countries)
        }
        return data
    }

    @TypeConverter
    fun toImagesList(jsonProductionCountries: String): List<ProductionCountry> {
        val notesType = object : TypeToken<List<ProductionCountry>>() {}.type
        return Gson().fromJson<List<ProductionCountry>>(jsonProductionCountries, notesType)
    }
}