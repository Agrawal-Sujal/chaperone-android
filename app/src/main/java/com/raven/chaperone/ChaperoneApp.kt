package com.raven.chaperone

import android.app.Application
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChaperoneApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyD_2ddG-8Yd-6TZh6kHiGhPPDYz2oHJ1wE")

        }
    }
}