package com.raven.chaperone.data.local.appPref

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.appDataStore by preferencesDataStore(name = "app_pref")

class AppPref(private val context: Context) {

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val TOKEN = stringPreferencesKey("token")

        private val EMAIL = stringPreferencesKey("email")
    }

    val token: Flow<String?> = context.appDataStore.data.map { it[TOKEN] }


//    suspend fun saveToken( token: String) {
//        context.appDataStore.edit { prefs ->
//            prefs[TOKEN] = token
//        }
//    }
//
//    suspend fun clearToken() {
//        context.appDataStore.edit { it.clear() }
//    }
}