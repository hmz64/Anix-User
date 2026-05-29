package com.anix.app.core.network

import android.content.Context
import com.anix.app.core.di.ServiceLocator
import com.anix.app.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

object ApiClient {

    private val defaultBaseUrl: String = BuildConfig.BASE_URL
    private var baseUrl: String = defaultBaseUrl
    private var apiService: ApiService? = null
    private var okHttpClient: OkHttpClient? = null

    fun setBaseUrl(url: String) {
        baseUrl = url.trimEnd('/') + "/"
        apiService = null
    }

    fun resolveUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        if (path.startsWith("http://") || path.startsWith("https://")) return path
        return baseUrl.trimEnd('/') + "/" + path.trimStart('/')
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setFieldNamingPolicy(com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeHierarchyAdapter(String::class.java, StringAdapter())
            .create()
    }

    private class StringAdapter : JsonDeserializer<String> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): String? {
            return when {
                json.isJsonNull -> null
                json.isJsonPrimitive -> {
                    val p = json.asJsonPrimitive
                    when {
                        p.isNumber -> p.asNumber.toString()
                        p.isBoolean -> p.asBoolean.toString()
                        else -> p.asString
                    }
                }
                else -> json.toString()
            }
        }
    }

    fun getApiService(context: Context): ApiService {
        if (apiService == null) {
            val client = getOkHttpClient(context)
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService!!
    }

    private fun getOkHttpClient(context: Context): OkHttpClient {
        if (okHttpClient == null) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }

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

            okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }
        return okHttpClient!!
    }
}