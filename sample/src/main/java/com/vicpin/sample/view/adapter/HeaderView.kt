package com.vicpin.sample.view.adapter

import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.vicpin.kpresenteradapter.ViewHolder
import com.vicpin.sample.R
import com.vicpin.sample.model.Country
import com.vicpin.sample.presenter.HeaderPresenter

/**
 * Created by Victor on 25/06/2016.
 */
class HeaderView(itemView: View) : ViewHolder<Country>(itemView), HeaderPresenter.View {

    override var presenter = HeaderPresenter()

    @BindView(R.id.text)
    lateinit var mHeader: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun setNumItems(numItems: Int) {
        mHeader.text = context.getString(R.string.header, numItems)
    }
}
