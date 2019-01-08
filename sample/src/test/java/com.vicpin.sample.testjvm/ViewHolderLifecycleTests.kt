package com.vicpin.sample.testjvm

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import com.vicpin.kpresenteradapter.PresenterAdapter
import com.vicpin.sample.R
import com.vicpin.sample.di.Injector
import com.vicpin.sample.model.Country
import com.vicpin.sample.model.IRepository
import com.vicpin.sample.presenter.CountryPresenter
import com.vicpin.sample.view.activity.ManualBindingActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ViewHolderLifecycleTests {

    private val TEST_PAGE_SIZE = 10
    private var testRepository = TestRepository(TEST_PAGE_SIZE)
    private lateinit var countryPresenter: CountryPresenter

    @Before
    fun setUp() {
        //We will use a spied injector, in order to return a specific presenter instance when requested
        Injector.set(spy(Injector.get()))
        Injector.get().setCountryRepository(testRepository)

        //We sill use a spied country presenter to verify lifecycle method invocations
        countryPresenter = spy(CountryPresenter())
        whenever(Injector.get().getCountryPresenter()).thenReturn(countryPresenter)
    }

    @Test
    fun when_thereIsNoData_then_headerShowsNoItems() {
        //Given some items
        val itemsSize = 5
        initRepositoryWihtCountries(itemsSize)     

        //When: activity starts
        launchActivity<ManualBindingActivity>().onActivity {

            //Then: oncreate and onattached methods are called one time per item
            verify(countryPresenter, times(itemsSize)).onCreate()
            verify(countryPresenter, times(itemsSize)).onAttached()
        }
    }

    @Test
    fun when_scroll_then_onDetachedAndOnDestroyIsCalled() {
        //Given a large amount of items
        val itemsSize = 50
        initRepositoryWihtCountries(itemsSize)

        //When: activity starts
        launchActivity<ManualBindingActivity>().onActivity {

            //Calculate visible amount of items when activity starts
            val recycler = it.findViewById<RecyclerView>(R.id.recycler)
            val lastPosition = (recycler.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            val totalVisibleItems = lastPosition - (recycler.adapter as PresenterAdapter<Country>).getHeadersCount() + 1

            //Then: verify that onCreate and onAttached is called for each item
            verify(countryPresenter, times(totalVisibleItems)).onCreate()
            verify(countryPresenter, times(totalVisibleItems)).onAttached()

            //After: scroll to second group of visible items
            onViewId(R.id.recycler).scrollTo(totalVisibleItems * 2)

            //Then: verify that onCreate and onAttached is called for each item again
            verify(countryPresenter, times(totalVisibleItems * 2)).onCreate()
            verify(countryPresenter, times(totalVisibleItems * 2)).onAttached()

            //Verify that onDetached is called for no visible items
            verify(countryPresenter, times(totalVisibleItems)).onDetached()
            
            //Verify that onDestroy is called at leas once (we cannot assure how many times is going to be called)
            verify(countryPresenter, atLeastOnce()).onDestroy()
        }
    }

    fun initRepositoryWihtCountries(size: Int) {
        testRepository.items = List(size) {  Country("Country $it", R.mipmap.ic_launcher)  }
    }

}

class TestRepository(pageSize: Int): IRepository<Country> {
    override val PAGE_SIZE = pageSize
    override var items = listOf<Country>()
}