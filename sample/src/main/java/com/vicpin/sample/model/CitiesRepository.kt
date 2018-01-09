package com.vicpin.sample.model

import android.content.res.Resources
import com.vicpin.sample.R
import java.util.*

/**
 * Created by Victor on 25/06/2016.
 */
object CitiesRepository {

    private val PAGE_SIZE = 30
    private val IMAGE_URL = "http://www.luxus-india.com/wp-content/uploads/2015/12/stock-vector-flat-city-icon-209365492.png"

    fun getItems(resources: Resources) = resources.getStringArray(R.array.india_places).map { Town(it, IMAGE_URL) }

    fun getItemsPage(resources: Resources, page: Int): List<Town> {

        val startIndex = page * PAGE_SIZE
        var endIndex = page * PAGE_SIZE + PAGE_SIZE - 1
        val countries = getItems(resources)

        if (startIndex >= countries.size) {
            return ArrayList()
        }

        if (endIndex > countries.size) {
            endIndex = countries.size
        }

        return countries.subList(startIndex, endIndex)
    }
}