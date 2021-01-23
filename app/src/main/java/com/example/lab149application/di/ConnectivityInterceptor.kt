package com.example.lab149application.di

import okhttp3.Interceptor
import java.io.IOException

class ConnectivityInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        if (!WifiService.instance.isOnline()) {
            throw IOException("No internet connection")
        } else {
            return chain.proceed(chain.request())
        }
    }
}
