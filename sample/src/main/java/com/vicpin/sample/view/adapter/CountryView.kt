package com.vicpin.sample.view.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.vicpin.kpresenteradapter.ViewHolder
import com.vicpin.sample.R
import com.vicpin.sample.model.Country
import com.vicpin.sample.presenter.CountryPresenter
import com.vicpin.sample.view.interfaces.ItemDeletedListener
import com.vicpin.sample.view.interfaces.ItemRecycledListener

/**
 * Created by Victor on 25/06/2016.
 */
class CountryView(itemView: View) : ViewHolder<Country>(itemView), CountryPresenter.View {

    override var presenter = CountryPresenter()

    @BindView(R.id.countryName)
    lateinit var mCountryName: TextView

    @BindView(R.id.textInfo)
    lateinit var mTextInfo: TextView

    @BindView(R.id.imageView)
    lateinit var mImageView: ImageView

    init {
      ButterKnife.bind(this, itemView)
    }

    override fun setCountryName(text: String) {
        mCountryName.text = text
    }

    override fun setInfo(info: String) {
        mTextInfo.text = info
    }

    override fun setImage(resourceId: Int) {
        mImageView.setImageResource(resourceId)
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

    @OnClick(R.id.deleteButton)
    fun onDeleteButtonClicked(v: View) {
        presenter.onDeleteItem()
    }

    override fun deleteItem(item: Country) {
        (customListener as? ItemDeletedListener<Country>)?.onItemDeleted(item)
    }
}
