package com.vicpin.sample.view.presenter


import com.vicpin.kpresenteradapter.ViewHolderPresenter
import com.vicpin.sample.model.Country

/**
 * Created by Victor on 25/06/2016.
 */
class HeaderPresenter : ViewHolderPresenter<Country, HeaderPresenter.View>() {

    override fun onCreate() {
        showNumItems()
    }

    fun showNumItems() {
        view?.setNumItems(dataCollection.size)
    }

    interface View {
        fun setNumItems(numItems: Int)
    }
}
