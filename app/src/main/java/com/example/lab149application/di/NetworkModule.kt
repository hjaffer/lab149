package com.example.lab149application.di

import android.content.Context
import com.example.lab149application.business.domain.models.SnapDao
import com.example.lab149application.business.domain.util.EntityMapper
import com.example.lab149application.business.network.NetworkDataSource
import com.example.lab149application.business.network.NetworkDataSourceImpl
import com.example.lab149application.framework.network.SnapRetrofit
import com.example.lab149application.framework.network.SnapRetrofitService
import com.example.lab149application.framework.network.SnapRetrofitServiceImpl
import com.example.lab149application.framework.network.mappers.SnapNetworkMapper
import com.example.lab149application.framework.network.model.snap.SnapObject
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val API_URL = "https://hoi4nusv56.execute-api.us-east-1.amazonaws.com/iositems/"
private const val CONNECT_TIMEOUT_SEC = 10L
private const val READ_TIMEOUT_SEC = 10L
private const val WRITE_TIMEOUT_SEC = 10L

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate?>?,
                                                    authType: String?) = Unit

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate?>?,
                                                    authType: String?) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )
            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()
    }

    @Singleton
    @Provides
    fun provideSnapRetrofit(gson: Gson, @ApplicationContext context: Context): Retrofit.Builder {
        WifiService.instance.initializeWithApplicationContext(context)
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        val okHttpClient = getUnsafeOkHttpClient()
            .addInterceptor(logging)
            .addInterceptor(ConnectivityInterceptor())
            .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SEC, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideSnapService(retrofit: Retrofit.Builder): SnapRetrofit {
        return retrofit
            .build()
            .create(SnapRetrofit::class.java)
    }

    @Singleton
    @Provides
    fun provideSnapRetrofitService(
        snapRetrofit: SnapRetrofit
    ): SnapRetrofitService {
        return SnapRetrofitServiceImpl(snapRetrofit)
    }

    @Singleton
    @Provides
    fun provideSnapNetworkMapper(): EntityMapper<SnapObject, SnapDao> {
        return SnapNetworkMapper()
    }

    @Singleton
    @Provides
    fun provideSnapNetworkDataSource(
        snapRetrofitService: SnapRetrofitService,
        snapNetworkMapper: SnapNetworkMapper
    ): NetworkDataSource {
        return NetworkDataSourceImpl(snapRetrofitService, snapNetworkMapper)
    }
}
