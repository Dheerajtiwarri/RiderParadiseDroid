package com.droid.riderparadise.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "session")

/** Persists the signed-in user id and onboarding completion flag. */
@Singleton
class SessionStore @Inject constructor(
    private val context: Context,
) {
    private val keyUserId = stringPreferencesKey("user_id")
    private val keyOnboarded = booleanPreferencesKey("onboarded")
    private val keyToken = stringPreferencesKey("auth_token")

    val userIdFlow: Flow<String?> = context.dataStore.data.map { it[keyUserId] }

    suspend fun userId(): String? = context.dataStore.data.first()[keyUserId]

    suspend fun isOnboarded(): Boolean = context.dataStore.data.first()[keyOnboarded] ?: false

    /** Bearer token issued by the backend on OTP verification (BACKEND.md §4). */
    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[keyToken] }

    suspend fun token(): String? = context.dataStore.data.first()[keyToken]

    suspend fun setToken(token: String) {
        context.dataStore.edit { it[keyToken] = token }
    }

    suspend fun setUserId(id: String) {
        context.dataStore.edit { it[keyUserId] = id }
    }

    suspend fun setOnboarded(value: Boolean) {
        context.dataStore.edit { it[keyOnboarded] = value }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
