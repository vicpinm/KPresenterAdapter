package com.vicpin.sample.view.adapter

import android.view.View
import com.vicpin.kpresenteradapter.ViewHolder
import com.vicpin.sample.R
import com.vicpin.sample.model.Country
import com.vicpin.sample.view.presenter.HeaderPresenter
import kotlinx.android.synthetic.main.adapter_header.*

/**
 * Created by Victor on 25/06/2016.
 */
class HeaderView(itemView: View) : ViewHolder<Country>(itemView), HeaderPresenter.View {

    override var presenter = HeaderPresenter()


    override fun setNumItems(numItems: Int) {
        headerText.text = context.getString(R.string.header, numItems)
    }
}
