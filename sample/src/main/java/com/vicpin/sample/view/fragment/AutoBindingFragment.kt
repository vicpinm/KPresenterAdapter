package com.vicpin.sample.view.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import com.vicpin.kpresenteradapter.PresenterAdapter
import com.vicpin.kpresenteradapter.extensions.inflate
import com.vicpin.sample.R
import com.vicpin.sample.extensions.showToast
import com.vicpin.sample.model.CitiesRepository
import com.vicpin.sample.model.Town
import kotlinx.android.synthetic.main.fragment_main.*



/**
 * Created by Victor on 25/06/2016.
 */
class AutoBindingFragment : Fragment() {

    //Falta: la clase *ViewHolderParent no la reconoce al importarla, no se genera en el directorio correcto

    private var currentPage: Int = 0
    private lateinit var adapter: PresenterAdapter<Town>

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
        val data = CitiesRepository.getItemsPage(resources, 0)
        //adapter = TownPresenterAdapter(R.layout.adapter_town)
        adapter.setData(data)
        adapter.enableLoadMore { onLoadMore() }


    }

    fun appendListeners() {
        adapter.apply {
            itemClickListener = { item, view -> showToast("Country clicked: " + item.name) }
            itemLongClickListener = { item, view -> showToast("Country long pressed: " + item.name) }
            customListener = this@AutoBindingFragment
        }
    }

    fun setupRecyclerView() {
        list.layoutManager = LinearLayoutManager(activity)
        list.adapter = adapter
    }

    /**
     * Pagination listener. Simulates a 1500ms load delay.
     */
    fun onLoadMore() {
        Handler().postDelayed({
            currentPage++
            val newData = CitiesRepository.getItemsPage(resources, currentPage)
            if (newData.size > 0) {
                adapter.addData(newData)
            } else {
                adapter.disableLoadMore()
            }
        }, 1500)

    }


}
