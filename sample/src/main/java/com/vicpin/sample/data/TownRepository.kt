package com.vicpin.sample.data

import android.content.Context
import com.vicpin.sample.R
import com.vicpin.sample.model.IRepository
import com.vicpin.sample.model.Town

/**
 * Created by Victor on 25/06/2016.
 */
class TownRepository(context: Context?): IRepository<Town> {

    override val PAGE_SIZE = 30

    private val IMAGE_URL = "http://www.luxus-india.com/wp-content/uploads/2015/12/stock-vector-flat-city-icon-209365492.png"

    override var items = context?.resources?.getStringArray(R.array.india_places)?.map { Town(it, IMAGE_URL) } ?: listOf()

}