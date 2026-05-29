package com.anix.app

import android.app.Application
import android.os.Build
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.gif.AnimatedImageDecoder
import com.anix.app.core.di.ServiceLocator

class AnixApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("AnixCrash", "Uncaught exception on thread: ${thread.name}", throwable)
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(AnimatedImageDecoder.Factory())
                }
            }
            .build()
    }
}
