package com.droid.riderparadise.core.network

import com.droid.riderparadise.data.datastore.SessionStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/** Attaches `Authorization: Bearer <token>` from the session to every request (BACKEND.md §4). */
@Singleton
class AuthInterceptor @Inject constructor(
    private val session: SessionStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { session.token() }
        val request = chain.request()
        val authed = if (token.isNullOrBlank()) {
            request
        } else {
            request.newBuilder().header("Authorization", "Bearer $token").build()
        }
        return chain.proceed(authed)
    }
}
