package com.vicpin.sample.view.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vicpin.kpresenteradapter.PresenterAdapter
import com.vicpin.kpresenteradapter.SimplePresenterAdapter
import com.vicpin.kpresenteradapter.SingleLinePresenterAdapter
import com.vicpin.kpresenteradapter.extensions.inflate
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
class ManualBindingFragment : Fragment(), ItemRecycledListener, ItemDeletedListener<Country> {

    private var lastPresentersRecycled: Int = 0
    private var currentPage: Int = 0
    private lateinit var adapter: PresenterAdapter<Country>
    private var isSingleAdapter = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = container?.inflate(R.layout.fragment_main)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    fun initView(){
        setupAdapter()
        appendListeners()
        setupRecyclerView()
    }

    fun setupAdapter() {
        val data = CountryRepository.getItemsPage(resources, 0)
        if(isSingleAdapter){
            adapter = SingleLinePresenterAdapter(R.layout.adapter_country_single_line)
        }
        else {
            adapter = SimplePresenterAdapter(CountryView::class, R.layout.adapter_country)
        }
        adapter.setData(data)
        adapter.addHeader(ViewInfo(HeaderView::class, R.layout.adapter_header))
        adapter.enableLoadMore { onLoadMore() }


    }

    fun appendListeners() {
        adapter.apply {
            itemClickListener = { item, view -> showToast("Country clicked: " + item.name) }
            itemLongClickListener = { item, view -> showToast("Country long pressed: " + item.name) }
            customListener = this@ManualBindingFragment
        }
    }

    fun setupRecyclerView() {
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
    }

    override fun onItemRecycled(presenterId: Int) {
        lastPresenterDestroyed.text = "Last presenters recycled: $lastPresentersRecycled - $presenterId"
        lastPresentersRecycled = presenterId
    }

    /**
     * Pagination listener. Simulates a 1500ms load delay.
     */
    fun onLoadMore() {
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
        adapter.updateHeaders()
    }

    fun toggleAdapter() {
        isSingleAdapter = !isSingleAdapter
        initView()
    }
}