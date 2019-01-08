package com.vicpin.sample.testjvm

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
