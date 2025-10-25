package com.raven.chaperone.di

import com.raven.chaperone.data.local.appPref.AppPref
import com.raven.chaperone.utils.Constants
import com.raven.chaperone.utils.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    private val okHttpClient by lazy {
        OkHttpClient
            .Builder()
            .build()
    }

    private fun constructRetrofit(appPreferences: AppPref): Retrofit =
        Retrofit.Builder()
            .client(
                okHttpClient
                    .newBuilder()
                    .addInterceptor(HeaderInterceptor(appPreferences))
                    // .addInterceptor (HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .build(),
            )
            .baseUrl(Constants.CHAPERONE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}