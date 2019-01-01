package com.vicpin.sample.test

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vicpin.sample.R
import com.vicpin.sample.di.Injector
import com.vicpin.sample.extensions.IdlingResourceManager
import com.vicpin.sample.model.Country
import com.vicpin.sample.model.IRepository
import com.vicpin.sample.view.activity.ManualBindingActivity
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test form ManualBindingActivity
 *
 * IMPORTANT: Animations should be disabled on device
 */
@RunWith(AndroidJUnit4::class)
class ManualBindingActivityTests {

    val TEST_PAGE_SIZE = 10
    var testRepository = TestRepository(TEST_PAGE_SIZE)

    @Before
    fun setUp() {
        Injector.get().setCountryRepository(testRepository)
        IdlingResourceManager.autoRegister = true
    }

    @Test
    fun when_thereIsNoData_then_headerShowsNoItems() {
        //Given: no items
        testRepository.items = listOf()

        //When: activity starts
        launchActivity<ManualBindingActivity>()

        //Then: header shows no items
        val headerText = getResources().getString(R.string.header, 0)
        onViewId(R.id.headerText).check(matches(withText(headerText)))
    }

    @Test
    fun when_dataSize_fitsInSinglePage_then_headerShowsItemsAmount() {
        //Given: some items under page limit
        val items = TEST_PAGE_SIZE / 2
        initRepositoryWihtCountries(items)

        //When: activity starts
        launchActivity<ManualBindingActivity>()

        //Then: header shows correct amount of items
        val headerText = getResources().getString(R.string.header, items)
        onViewId(R.id.headerText).check(matches(withText(headerText)))

    }

    @Test
    fun when_dataSize_doesNotFitInSinglePage_then_headerShowsItemsAmountOfFirstPageOnly() {
        //Given: some items over page limit
        val items = TEST_PAGE_SIZE * 2
        initRepositoryWihtCountries(items)

        //When: activity starts
        launchActivity<ManualBindingActivity>()

        //Then: only first page is loaded, header should show only first page size
        val headerText = getResources().getString(R.string.header, TEST_PAGE_SIZE)
        onViewId(R.id.headerText).check(matches(withText(headerText)))

    }

    @Test
    fun when_scrollToSecondPage_then_headerShowsItemsAmountOfTwoFirstPages() {
        //Given: some items over page limit
        val items = TEST_PAGE_SIZE * 3
        initRepositoryWihtCountries(items)

        //When: activity starts and we scroll to second page and when it is loaded, scroll to top to show header again
        launchActivity<ManualBindingActivity>()
        onViewId(R.id.recycler).apply {
            scrollTo(TEST_PAGE_SIZE + 1)
            scrollTo(0)
        }

        //Then: after second page is loaded, the amount of items in header should be TEST_PAGE_SIZE * 2
        val headerText = getResources().getString(R.string.header, TEST_PAGE_SIZE * 2)
        onViewId(R.id.headerText).check(matches(withText(headerText)))
    }


    @Test
    fun when_thereIsNoData_then_listHasNoItems() {
        //Given: no items
        testRepository.items = listOf()

        //When: activity starts
        launchActivity<ManualBindingActivity>()

        //Then: list has no items (header and load more progress are not taken into account)
        onViewId(R.id.recycler).check(matches(hasAdapterSize(0)))
    }

    @Test
    fun when_dataSize_fitsInSinglePage_then_listHasItems() {
        //Given: some items under page limit
        val items = TEST_PAGE_SIZE / 2
        initRepositoryWihtCountries(items)

        //When: activity starts
        launchActivity<ManualBindingActivity>()

        //Then: list has items
        onViewId(R.id.recycler).check(matches(hasAdapterSize(items)))
    }

    @Test
    fun when_thereAreMoreThanOnePageOfData_then_listHasFirstPageOfData() {
        //Given: some items under page limit
        val items = TEST_PAGE_SIZE * 3
        initRepositoryWihtCountries(items)

        //When: activity starts
        launchActivity<ManualBindingActivity>()

        //Then: list has first page loaded
        onViewId(R.id.recycler).check(matches(hasAdapterSize(TEST_PAGE_SIZE)))
    }

    @Test
    fun when_thereAreMoreThanOnePageOfData_andScrollToSecondPage_then_listHasTwoPagesOfData() {
        //Given: some items over page limit
        val items = TEST_PAGE_SIZE * 3
        initRepositoryWihtCountries(items)

        //When: activity starts and scroll to second page
        launchActivity<ManualBindingActivity>()
        onViewId(R.id.recycler).scrollTo(TEST_PAGE_SIZE + 1)

        //Then: list has first page loaded
        onViewId(R.id.recycler).check(matches(hasAdapterSize(TEST_PAGE_SIZE * 2)))
    }

    @Test
    fun when_thereIsData_then_checkRowsContainsText() {
        //Given: some items under page limit
        val items = TEST_PAGE_SIZE * 3
        initRepositoryWihtCountries(items)

        //When: activity starts
        launchActivity<ManualBindingActivity>()

        //Then: iterate for each cell and check displayed text
        testRepository.items.forEachIndexed { index, country ->
            onViewId(R.id.recycler).scrollTo(index + 1)
            onView(withText(country.name)).check(matches(isDisplayed()))
        }
    }


    @Test
    fun when_newPageIsLoading_then_progressIsVisibleWhileDataIsLoading() {
        //Given: some items over page limit
        val items = TEST_PAGE_SIZE * 3
        initRepositoryWihtCountries(items)
        IdlingResourceManager.autoRegister = false

        //When: activity starts and scrolls to bottom
        launchActivity<ManualBindingActivity>()
        onViewId(R.id.recycler).scrollTo(TEST_PAGE_SIZE + 1)

        //Then: at first, progress is displayed
        onViewId(R.id.progressBar).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        //Register idling resource to wait until data is loaded and check that progress is not visible anymore
        IdlingResourceManager.register()
        onViewId(R.id.progressBar).check(isNotDisplayed())
    }

    @Test
    fun when_firstPageLoaded_and_footerIsVisibleWithoutScrooling_then_progressIsVisibleWhileDataIsLoading() {
        //Given: few items so that footer is visible on screen
        val items = 2
        initRepositoryWihtCountries(items)
        IdlingResourceManager.autoRegister = false

        //When: activity starts
        launchActivity<ManualBindingActivity>()

        //Then: at first, progress is displayed
        onViewId(R.id.progressBar).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        //Register idling resource to wait until data is loaded and check that progress is not visible anymore
        IdlingResourceManager.register()
        onViewId(R.id.progressBar).check(isNotDisplayed())

        //Check that data loaded after progress bar hidden is the same
        onViewId(R.id.recycler).check(matches(hasAdapterSize(items)))
    }


    @Test
    fun when_scrollToLastPage_then_allItemsAreLoaded() {
        //Given: some items over page limit
        val items = TEST_PAGE_SIZE * 5
        initRepositoryWihtCountries(items)

        //When: activity starts and we scroll to last page
        launchActivity<ManualBindingActivity>()
        for(i in 1..5) {
            onViewId(R.id.recycler).scrollTo((TEST_PAGE_SIZE * i) + 1)
        }

        //Then: after last page is loaded, the amount of items loaded are TEST_PAGE_SIZE * 5
        onViewId(R.id.recycler).check(matches(hasAdapterSize(items)))
    }

    @Test
    fun when_itemDeleted_then_itemIsHiddenAndHeaderIsUpdated() {
        //Given: some items over page limit
        val items = 2
        initRepositoryWihtCountries(items)

        //When: activity starts and delete first item
        launchActivity<ManualBindingActivity>()
        onView(allOf(withId(R.id.deleteButton), hasSibling(withText("Country 1")))).perform(click())

        //Then: items size are decremented, and header is updated
        onViewId(R.id.recycler).check(matches(hasAdapterSize(items - 1)))

        val headerText = getResources().getString(R.string.header, (items - 1))
        onViewId(R.id.headerText).check(matches(withText(headerText)))

        //Check correct item is hidden
        onView(allOf(withId(R.id.deleteButton), hasSibling(withText("Country 1")))).check(isNotDisplayed())
    }


    @Test
    fun when_allItemsAreDeleted_then_listHasNoItems() {
        //Given: some items over page limit
        val items = TEST_PAGE_SIZE * 2
        initRepositoryWihtCountries(items)

        //When: activity starts and delete first item
        launchActivity<ManualBindingActivity>()
        testRepository.items.forEachIndexed { index, country ->
            onView(allOf(withId(R.id.deleteButton), hasSibling(withText(country.name)))).perform(click())
        }

        //Then: items size are decremented, and header is updated
        onViewId(R.id.recycler).check(matches(hasAdapterSize(0)))

        val headerText = getResources().getString(R.string.header, 0)
        onViewId(R.id.headerText).check(matches(withText(headerText)))
    }

    @Test
    fun when_itemClicked_then_toastAppearsWithCorrectText() {
        //Given: some items
        val positionClicked = 0
        initRepositoryWihtCountries(TEST_PAGE_SIZE)
        IdlingResourceManager.autoRegister = false

        //When: activity starts and item is clicked
        launchActivity<ManualBindingActivity>()
        onViewId(R.id.recycler).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(positionClicked + 1, click()))

        //Then: toast is showed
        val toastText = getResources().getString(R.string.toast_message, testRepository.items[positionClicked].name)
        onView(withText(toastText)).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }

    fun initRepositoryWihtCountries(size: Int) {
        testRepository.items = List(size) {  Country("Country $it", R.mipmap.ic_launcher)  }
    }

}


class TestRepository(pageSize: Int): IRepository<Country> {
    override val PAGE_SIZE = pageSize
    override var items = listOf<Country>()
}








