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
import com.vicpin.sample.model.IRepository
import com.vicpin.sample.model.Town
import com.vicpin.sample.view.activity.AutoBindingActivity
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test form AutoBindingActivity
 *
 * IMPORTANT: Animations should be disabled on device
 */
@RunWith(AndroidJUnit4::class)
class AutoBindingActivityTests {

    val TEST_PAGE_SIZE = 10
    var testRepository = TownTestRepository(TEST_PAGE_SIZE)
    val TOWN_NAME_PREFIX = "Town test:"

    @Before
    fun setUp() {
        Injector.get().setTownRepository(testRepository)
        IdlingResourceManager.autoRegister = true
    }


    @Test
    fun when_thereIsNoData_then_listHasNoItems() {
        //Given: no items
        testRepository.items = listOf()

        //When: activity starts
        launchActivity<AutoBindingActivity>()

        //Then: list has no items
        onViewId(R.id.recycler).check(matches(hasAdapterSize(0)))
    }

    @Test
    fun when_dataSize_fitsInSinglePage_then_listHasItems() {
        //Given: some items under page limit
        val items = TEST_PAGE_SIZE / 2
        initRepositoryWithTowns(items)

        //When: activity starts
        launchActivity<AutoBindingActivity>()

        //Then: list has items
        onViewId(R.id.recycler).check(matches(hasAdapterSize(items)))
    }

    @Test
    fun when_thereAreMoreThanOnePageOfData_then_listHasFirstPageOfData() {
        //Given: some items under page limit
        val items = TEST_PAGE_SIZE * 3
        initRepositoryWithTowns(items)

        //When: activity starts
        launchActivity<AutoBindingActivity>()

        //Then: list has first page loaded
        onViewId(R.id.recycler).check(matches(hasAdapterSize(TEST_PAGE_SIZE)))
    }

    @Test
    fun when_thereAreMoreThanOnePageOfData_andScrollToSecondPage_then_listHasTwoPagesOfData() {
        //Given: some items over page limit
        val items = TEST_PAGE_SIZE * 3
        initRepositoryWithTowns(items)

        //When: activity starts and scroll to second page
        launchActivity<AutoBindingActivity>()
        onViewId(R.id.recycler).scrollTo(TEST_PAGE_SIZE + 1)

        //Then: list has first page loaded
        onViewId(R.id.recycler).check(matches(hasAdapterSize(TEST_PAGE_SIZE * 2)))
    }

    @Test
    fun when_thereIsData_then_checkRowsContainsText() {
        //Given: some items under page limit
        val items = TEST_PAGE_SIZE * 3
        initRepositoryWithTowns(items)

        //When: activity starts
        launchActivity<AutoBindingActivity>()

        //Then: iterate for each cell and check displayed text
        testRepository.items.forEachIndexed { index, town ->
            onViewId(R.id.recycler).scrollTo(index + 1)
            onView(withText(town.name)).check(matches(isDisplayed()))
        }
    }


    @Test
    fun when_newPageIsLoading_then_progressIsVisibleWhileDataIsLoading() {
        //Given: some items over page limit
        val items = TEST_PAGE_SIZE * 3
        initRepositoryWithTowns(items)
        IdlingResourceManager.autoRegister = false

        //When: activity starts and scrolls to bottom
        launchActivity<AutoBindingActivity>()
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
        initRepositoryWithTowns(items)
        IdlingResourceManager.autoRegister = false

        //When: activity starts
        launchActivity<AutoBindingActivity>()

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
        initRepositoryWithTowns(items)

        //When: activity starts and we scroll to last page
        launchActivity<AutoBindingActivity>()
        for(i in 1..5) {
            onViewId(R.id.recycler).scrollTo((TEST_PAGE_SIZE * i) + 1)
        }

        //Then: after last page is loaded, the amount of items loaded are TEST_PAGE_SIZE * 5
        onViewId(R.id.recycler).check(matches(hasAdapterSize(items)))
    }

    @Test
    fun when_itemDeleted_then_itemIsHidden() {
        //Given: some items over page limit
        val items = 2
        initRepositoryWithTowns(items)

        //When: activity starts and delete first item
        launchActivity<AutoBindingActivity>()
        onView(allOf(withId(R.id.deleteButton), hasSibling(withText("$TOWN_NAME_PREFIX 1")))).perform(click())

        //Then: items size are decremented
        onViewId(R.id.recycler).check(matches(hasAdapterSize(items - 1)))

        //Check correct item is hidden
        onView(allOf(withId(R.id.deleteButton), hasSibling(withText("$TOWN_NAME_PREFIX 1")))).check(isNotDisplayed())
    }


    @Test
    fun when_allItemsAreDeleted_then_listHasNoItems() {
        //Given: some items over page limit
        val items = TEST_PAGE_SIZE * 2
        initRepositoryWithTowns(items)

        //When: activity starts and delete first item
        launchActivity<AutoBindingActivity>()
        testRepository.items.forEachIndexed { index, town ->
            onView(allOf(withId(R.id.deleteButton), hasSibling(withText(town.name)))).perform(click())
        }

        //Then: items size are decremented
        onViewId(R.id.recycler).check(matches(hasAdapterSize(0)))
    }

    @Test
    fun when_itemClicked_then_toastAppearsWithCorrectText() {
        //Given: some items
        val positionClicked = 0
        initRepositoryWithTowns(TEST_PAGE_SIZE)
        IdlingResourceManager.autoRegister = false

        //When: activity starts and item is clicked
        launchActivity<AutoBindingActivity>()
        onViewId(R.id.recycler).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(positionClicked + 1, click()))

        //Then: toast is showed
        val toastText = getResources().getString(R.string.toast_message, testRepository.items[positionClicked].name)
        onView(withText(toastText)).inRoot(ToastMatcher()).check(matches(isDisplayed()))
    }

    fun initRepositoryWithTowns(size: Int) {
        testRepository.items = List(size) {  Town("$TOWN_NAME_PREFIX $it", "https://cdn0.iconfinder.com/data/icons/realty-1/512/houses-512.png")  }
    }

}


class TownTestRepository(pageSize: Int): IRepository<Town> {
    override val PAGE_SIZE = pageSize
    override var items = listOf<Town>()
}








