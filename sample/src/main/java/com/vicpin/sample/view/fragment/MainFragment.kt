package com.vicpin.sample.view.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vicpin.kpresenteradapter.PresenterAdapter
import com.vicpin.kpresenteradapter.SimplePresenterAdapter
import com.vicpin.kpresenteradapter.ViewHolder
import com.vicpin.kpresenteradapter.extensions.inflate
import com.vicpin.kpresenteradapter.listeners.ItemClickListener
import com.vicpin.kpresenteradapter.listeners.ItemLongClickListener
import com.vicpin.kpresenteradapter.listeners.OnLoadMoreListener
import com.vicpin.kpresenteradapter.model.ViewInfo
import com.vicpin.sample.R
import com.vicpin.sample.extensions.showToast
import com.vicpin.sample.model.Country
import com.vicpin.sample.model.CountryRepository
import com.vicpin.sample.view.adapter.CountryView
import com.vicpin.sample.view.adapter.HeaderView
import com.vicpin.sample.view.interfaces.ItemDeletedListener
import com.vicpin.sample.view.interfaces.ItemRecycledListener
import kotlinx.android.synthetic.main.fragment_main.*



/**
 * Created by Victor on 25/06/2016.
 */
class MainFragment : Fragment(), ItemClickListener<Country>, ItemLongClickListener<Country>, ItemRecycledListener, ItemDeletedListener<Country>, OnLoadMoreListener {

    private var lastPresentersRecycled: Int = 0
    private var currentPage: Int = 0
    private lateinit var adapter: PresenterAdapter<Country>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = container?.inflate(R.layout.fragment_main)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupAdapter()
        appendListeners()
        setupRecyclerView()
    }

    fun setupAdapter() {
        val data = CountryRepository.getItemsPage(resources, 0)
        adapter = SimplePresenterAdapter(CountryView::class, R.layout.adapter_country)
        adapter.setData(data)
        adapter.addHeader(ViewInfo(HeaderView::class, R.layout.adapter_header))
        adapter.enableLoadMore(this)
    }

    fun appendListeners() {
        adapter.apply {
            itemClickListener = this@MainFragment
            itemLongClickListener = this@MainFragment
            customListener = this@MainFragment
        }
    }

    fun setupRecyclerView() {
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
    }

    override fun onItemClick(item: Country, view: ViewHolder<Country>) = showToast("Country clicked: " + item.name)

    override fun onItemLongClick(item: Country, view: ViewHolder<Country>) = showToast("Country long pressed: " + item.name)

    override fun onItemRecycled(presenterId: Int) {
        lastPresenterDestroyed.text = "Last presenters recycled: $lastPresentersRecycled - $presenterId"
        lastPresentersRecycled = presenterId
    }

    /**
     * Pagination listener. Simulates a 1500ms load delay.
     */
    override fun onLoadMore() {
        Handler().postDelayed({
            currentPage++
            val newData = CountryRepository.getItemsPage(resources, currentPage)
            if (newData.size > 0) {
                adapter.addData(newData)
            } else {
                adapter.disableLoadMore()
            }
        }, 1500)

    }

    override fun onItemDeleted(item: Country) {
        adapter.removeItem(item)
        adapter.updateHeaders()
    }
}
