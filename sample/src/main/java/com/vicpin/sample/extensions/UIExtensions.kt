package com.vicpin.sample.extensions

import android.os.Handler
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Victor on 01/11/2016.
 */

fun androidx.fragment.app.Fragment.showToast(text: String, duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(activity, text, duration).show()
}

fun startIdlingResource() = IdlingResourceManager.start()
fun finishIdlingResource() = IdlingResourceManager.finish()

object IdlingResourceManager {

    private var instance: SimpleIdlingResource? = null
    var autoRegister = true

    fun register() {
        if(instance == null) {
            start()
        }
        IdlingRegistry.getInstance().register(instance)
    }

    fun start() {
        if(instance == null) {
            instance = SimpleIdlingResource()
            if(autoRegister) {
                IdlingRegistry.getInstance().register(instance)
            }
        }
    }

    fun finish() {
        instance?.apply {
            Handler().postDelayed({
                finish()
                IdlingRegistry.getInstance().unregister(this)
                instance = null
            },100)

        }
    }
}

class SimpleIdlingResource: IdlingResource {

    private var callback: IdlingResource.ResourceCallback? = null
    private val isIdleNow = AtomicBoolean(false)

    override fun getName() = this.javaClass.name
    override fun isIdleNow() = isIdleNow.get()

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    fun finish() {
        this.isIdleNow.set(true)
        callback?.onTransitionToIdle()
    }
}

