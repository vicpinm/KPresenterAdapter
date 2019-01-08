package com.vicpin.sample.data

import android.content.Context
import com.vicpin.sample.R
import com.vicpin.sample.model.Country
import com.vicpin.sample.model.IRepository
import com.vicpin.sample.model.NamedItem
import com.vicpin.sample.model.Town

class MixedRepository(var context: Context?) : IRepository<NamedItem> {

    override val PAGE_SIZE = 10
    private val IMAGE_URL = "https://cdn0.iconfinder.com/data/icons/realty-1/512/houses-512.png"

    override var items = getMixedItems()

    private fun getMixedItems(): List<NamedItem> {
        val countries = context?.resources?.getStringArray(R.array.countries)?.map { Country(it, R.mipmap.ic_launcher) } ?: listOf()
        val towns = context?.resources?.getStringArray(R.array.india_places)?.map { Town(it, IMAGE_URL) } ?: listOf()

        return List((countries.size + towns.size) - 2) { index ->

            if(index % 2 == 0) {
                if((index/2) < countries.size) {
                    countries[index/2]
                } else {
                    towns[index/2]
                }
            } else {
                if((index/2) < towns.size) {
                    towns[index/2]
                } else {
                    countries[index/2]
                }
            }
        }
    }


}