package com.raven.chaperone.di

import com.raven.chaperone.data.local.appPref.AppPref
import com.raven.chaperone.services.remote.AccountsServices
import com.raven.chaperone.services.remote.AuthServices
import com.raven.chaperone.services.remote.PaymentServices
import com.raven.chaperone.services.remote.RequestsServices
import com.raven.chaperone.services.remote.SearchServices
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

    @Singleton
    @Provides
    fun provideSearchServices(appPreferences: AppPref): SearchServices =
        constructRetrofit(appPreferences)
            .create(SearchServices::class.java)

    @Singleton
    @Provides
    fun provideAccountsServices(appPreferences: AppPref): AccountsServices =
        constructRetrofit(appPreferences)
            .create(AccountsServices::class.java)

    @Singleton
    @Provides
    fun provideRequestsServices(appPreferences: AppPref): RequestsServices =
        constructRetrofit(appPreferences)
            .create(RequestsServices::class.java)

    @Singleton
    @Provides
    fun provideAuthServices(appPreferences: AppPref): AuthServices =
        constructRetrofit(appPreferences)
            .create(AuthServices::class.java)

    @Singleton
    @Provides
    fun providePaymentServices(appPreferences: AppPref): PaymentServices =
        constructRetrofit(appPreferences)
            .create(PaymentServices::class.java)

}