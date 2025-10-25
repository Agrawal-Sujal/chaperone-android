package com.raven.chaperone.di

import android.content.Context
import com.raven.chaperone.data.local.appPref.AppPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppPref(@ApplicationContext context: Context): AppPref = AppPref(context)

}