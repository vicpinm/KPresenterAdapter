package com.vicpin.sample.model

import android.content.res.Resources
import com.vicpin.sample.R
import java.util.*

/**
 * Created by Victor on 25/06/2016.
 */
object CountryRepository {

    private val PAGE_SIZE = 30

    fun getItems(resources: Resources) = resources.getStringArray(R.array.countries).map { Country(it, R.mipmap.ic_launcher) }

    fun getItemsPage(resources: Resources, page: Int): List<Country> {

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
