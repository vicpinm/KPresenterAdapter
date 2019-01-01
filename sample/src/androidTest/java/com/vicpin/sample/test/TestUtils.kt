package com.vicpin.sample.test

import android.app.Application
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import com.vicpin.kpresenteradapter.PresenterAdapter
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import android.view.WindowManager
import androidx.test.espresso.*

fun getResources() = ApplicationProvider.getApplicationContext<Application>().resources
fun ViewInteraction.scrollTo(position: Int) = perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position))
fun onViewId(@IdRes id: Int) = Espresso.onView(ViewMatchers.withId(id))
fun hasAdapterSize(size: Int) = AdapterCountMarcher(size)

class AdapterCountMarcher(val size: Int): BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

    var currentItemsSize = 0

    override fun describeTo(description: Description) {
        description.appendText("adapter expected items count: $size, found $currentItemsSize")
    }

    override fun matchesSafely(recycler: RecyclerView): Boolean {
        this.currentItemsSize = recycler.adapter?.itemCount ?: -1
        return (recycler.adapter as? PresenterAdapter<*>)?.getData()?.size == size
    }
}

fun isNotDisplayed(): ViewAssertion {
    return ViewAssertion { view, noView ->
        if (view != null && isDisplayed().matches(view)) {
            throw AssertionError("View is present in the hierarchy and Displayed: " + HumanReadables.describe(view))
        }
    }
}


class ToastMatcher : TypeSafeMatcher<Root>() {

    override fun describeTo(description: Description) {
        description.appendText("is toast")
    }

    public override fun matchesSafely(root: Root): Boolean {
        val type = root.windowLayoutParams.get().type
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            val windowToken = root.decorView.windowToken
            val appToken = root.decorView.applicationWindowToken
            if (windowToken === appToken) {
                // windowToken == appToken means this window isn't contained by any other windows.
                // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                return true
            }
        }
        return false
    }

}


