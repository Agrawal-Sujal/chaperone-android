package com.raven.chaperone.data.local.appPref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
        private val ID_VERIFIED = booleanPreferencesKey("id_verified")

        private val IS_WALKER = booleanPreferencesKey("is_walker")
    }

    val token: Flow<String?> = context.appDataStore.data.map { it[TOKEN] }
    val isIdVerified: Flow<Boolean?> = context.appDataStore.data.map { it[ID_VERIFIED] }

    val isWalker: Flow<Boolean?> = context.appDataStore.data.map { it[IS_WALKER] }

    suspend fun saveToken(token: String) {
        context.appDataStore.edit { prefs ->
            prefs[TOKEN] = token
        }
    }

    suspend fun idVerified() {
        context.appDataStore.edit { prefs ->
            prefs[ID_VERIFIED] = true
        }
    }

    suspend fun userRole(isWalker: Boolean) {
        context.appDataStore.edit { prefs ->
            prefs[IS_WALKER] = isWalker
        }
    }

    suspend fun clearToken() {
        context.appDataStore.edit { it.clear() }
    }
}