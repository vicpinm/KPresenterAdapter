package com.vicpin.sample

import android.app.Application
import com.vicpin.sample.di.Injector

class Application: Application() {

    override fun onCreate() {
        super.onCreate()
        Injector.init(this)
    }
}