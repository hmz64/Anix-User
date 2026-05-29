package com.anix.app

import android.app.Application
import android.util.Log
import com.anix.app.core.di.ServiceLocator

class AnixApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("AnixCrash", "Uncaught exception on thread: ${thread.name}", throwable)
        }
    }
}
