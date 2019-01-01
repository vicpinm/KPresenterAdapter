package com.vicpin.sample.model

import com.vicpin.kpresenteradapter.test.Identifable
import com.vicpin.sample.R

/**
 * Created by Victor on 25/06/2016.
 */
data class Country(var name: String, var imageResourceId: Int) : Identifable<String> {
    override fun getId() = name



}
