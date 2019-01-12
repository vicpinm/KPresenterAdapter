package com.vicpin.sample.view.adapter

import com.vicpin.kpresenteradapter.PresenterAdapter
import com.vicpin.kpresenteradapter.model.ViewInfo
import com.vicpin.sample.R
import com.vicpin.sample.model.Country
import com.vicpin.sample.model.NamedItem
import com.vicpin.sample.model.TownViewHolderParent

class MultiBindingAdapter : PresenterAdapter<NamedItem>() {

    override fun getViewInfo(position: Int): ViewInfo<NamedItem> {
        val item = getData()[position]
        return when(item) {
            is Country -> ViewInfo(CountryView::class, R.layout.adapter_country)
            else -> ViewInfo(TownViewHolderParent::class, R.layout.adapter_town)
        }
    }
}