package com.vicpin.sample.view.presenter


import android.util.Log
import com.vicpin.kpresenteradapter.ViewHolderPresenter
import com.vicpin.sample.model.Country

/**
 * Created by Victor on 25/06/2016.
 */
class CountryPresenter : ViewHolderPresenter<Country, CountryPresenter.View>() {

    override fun onCreate() {
        setCountryName()
        setPresenterId()
        setCountryImage()
    }

    fun setCountryName() {
        view?.setCountryName(data.name)
    }

    fun setPresenterId() {
        view?.setInfo("Rendered with presenter #" + presenterId)
    }

    override fun onDestroy() {
        view?.notifyPresenterDetroyed(presenterId)
    }

    fun setCountryImage() {
        view?.setImage(data.imageResourceId)
    }

    fun onDeleteItem() {
        deleteItemFromCollection()
        view?.deleteItem(data)
    }

    override fun onScrollStoped() {
        Log.d("scroll stopped","id $presenterId")
    }

    interface View {
        fun setCountryName(s: String)

        fun setInfo(s: String)

        fun setImage(resourceId: Int)

        fun notifyPresenterDetroyed(presenterId: Int)

        fun deleteItem(data: Country)
    }
}
