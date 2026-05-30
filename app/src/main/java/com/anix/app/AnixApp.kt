package com.anix.app

import android.app.Application
import android.os.Build
import android.util.Log
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.ImageDecoderDecoder
import coil.network.okhttp.OkHttpNetworkFetcherFactory
import com.anix.app.core.di.ServiceLocator
import okhttp3.Interceptor
import okhttp3.OkHttpClient

class AnixApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("AnixCrash", "Uncaught exception on thread: ${thread.name}", throwable)
        }
    }

    override fun newImageLoader(): ImageLoader {
        val authInterceptor = Interceptor { chain ->
            val token = ServiceLocator.getToken()
            val request = if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(request)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
        return ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                }
                add(OkHttpNetworkFetcherFactory { okHttpClient })
            }
            .build()
    }
}
