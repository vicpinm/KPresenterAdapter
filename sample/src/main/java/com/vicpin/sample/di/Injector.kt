package com.vicpin.sample.di

import android.content.Context
import com.vicpin.sample.data.CountryRepository
import com.vicpin.sample.data.TownRepository
import com.vicpin.sample.model.*
import com.vicpin.sample.view.presenter.CountryPresenter

class Injector private constructor(val context: Context) {

    companion object {
        private lateinit var instance: Injector
        fun get() = instance
        fun set(instance: Injector) {
            Companion.instance = instance
        }
        fun init(context: Context) {
            instance = Injector(context)
        }
    }

    private var countryRepository: IRepository<Country>? = null
    private var townRepository: IRepository<Town>? = null


    fun getCountryRepository(): IRepository<Country> {
        if (countryRepository == null) {
            countryRepository = CountryRepository(context)
        }

        return countryRepository!!
    }

    fun setCountryRepository(repository: IRepository<Country>) {
        this.countryRepository = repository
    }


    fun getTownRepository(): IRepository<Town> {
        if (townRepository == null) {
            townRepository = TownRepository(context)
        }

        return townRepository!!
    }

    fun setTownRepository(repository: IRepository<Town>) {
        this.townRepository = repository
    }

    fun getCountryPresenter() = CountryPresenter()


}