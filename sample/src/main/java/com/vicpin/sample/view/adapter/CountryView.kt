package com.vicpin.sample.view.adapter

import android.view.View

import com.vicpin.kpresenteradapter.ViewHolder

import com.vicpin.sample.model.Country
import com.vicpin.sample.presenter.CountryPresenter
import com.vicpin.sample.view.interfaces.ItemDeletedListener
import com.vicpin.sample.view.interfaces.ItemRecycledListener
import kotlinx.android.synthetic.main.adapter_country.*

/**
 * Created by Victor on 25/06/2016.
 */
class CountryView(itemView: View) : ViewHolder<Country>(itemView), CountryPresenter.View {

    override var presenter = CountryPresenter()

    init {
        deleteButton.setOnClickListener { presenter.onDeleteItem() }
    }

    override fun setCountryName(text: String) {
        countryName.text = text
    }

    override fun setInfo(info: String) {
        textInfo.text = info
    }

    override fun setImage(resourceId: Int) {
        imageView.setImageResource(resourceId)
    }

    /**
     * Example of activity/fragment comunication through custom listener instance
     * @param presenterId
     */
    override fun notifyPresenterDetroyed(presenterId: Int) {
        if (customListener != null) {
            (customListener as ItemRecycledListener).onItemRecycled(presenterId)
        }
    }

    override fun deleteItem(data: Country) {
        (customListener as? ItemDeletedListener<Country>)?.onItemDeleted(data)
    }
}
