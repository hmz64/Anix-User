package com.anix.app

import android.app.Application
import com.anix.app.core.di.ServiceLocator

class AnixApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
