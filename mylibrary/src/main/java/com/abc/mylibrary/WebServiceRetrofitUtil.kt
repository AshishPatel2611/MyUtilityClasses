package com.theportal.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.theportal.BuildConfig
import com.theportal.network.WebServices
import com.theportal.utils.ConnectionUtil
import com.theportal.utils.MyAppPreferenceUtils.getToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object WebServiceRetrofitUtil {

    var webService: WebServices? = null

    const val HEADER_vAuthToken = "vAuthToken"
    const val NO_INTERNET = "No internet connection"

    fun init(context: Context) {
        val gson = GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .create()

        val vAuthToken = getToken(context)

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val internetAccessInterceptor = Interceptor { chain ->
            //this will check internet connection availability every time request made by user
            if (ConnectionUtil.isDataConnectionAvailable(context)) {
                return@Interceptor chain.proceed(chain.request())
            } else {
                throw IOException(NO_INTERNET)
            }
        }

        val headerInterceptor = Interceptor { chain ->
            //this will add required headers to APIs
            return@Interceptor chain.proceed(
                chain.request().newBuilder()
                    .addHeader(HEADER_vAuthToken, vAuthToken)
                    .build()
            )
        }
        val okHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(internetAccessInterceptor)
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build()


        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        webService = retrofit!!.create(WebServices::class.java)
    }


    fun destroyInstance() {
        if (webService != null)
            webService = null
    }


}