package com.vicpin.sample.data

import android.content.Context
import com.vicpin.sample.R
import com.vicpin.sample.model.Country
import com.vicpin.sample.model.IRepository

/**
 * Created by Victor on 25/06/2016.
 */
class CountryRepository(context: Context?) : IRepository<Country> {

    override val PAGE_SIZE = 10

    override var items = context?.resources?.getStringArray(R.array.countries)?.map { Country(it, R.mipmap.ic_launcher) } ?: listOf()

}
