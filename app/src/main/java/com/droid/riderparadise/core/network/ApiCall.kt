package com.droid.riderparadise.core.network

import com.droid.riderparadise.core.result.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** Default poll cadence for "observe" flows that replaced Firestore listeners. */
const val POLL_INTERVAL_MS = 5_000L

/**
 * Unwraps an [ApiEnvelope] into a [Resource]. Network/HTTP failures and `error` envelopes
 * both map to [Resource.Error] so callers degrade gracefully (mirrors prior Firestore behavior).
 */
suspend fun <T> apiResource(call: suspend () -> ApiEnvelope<T>): Resource<T> = try {
    val env = call()
    val data = env.data
    when {
        data != null -> Resource.Success(data)
        env.error != null -> Resource.Error(env.error.message ?: "Request failed")
        else -> Resource.Error("Empty response")
    }
} catch (e: Exception) {
    Resource.Error(e.message ?: "Network error", e)
}

/** Returns the envelope's data or null on any failure (for nullable observe flows). */
suspend fun <T> apiOrNull(call: suspend () -> ApiEnvelope<T>): T? =
    runCatching { call().data }.getOrNull()

/**
 * Polls [block] every [intervalMs], emitting each result. Emits immediately, then repeats.
 * Replaces Firestore snapshot listeners for low-frequency data (BACKEND.md §8).
 */
fun <T> pollFlow(intervalMs: Long = POLL_INTERVAL_MS, block: suspend () -> T): Flow<T> = flow {
    while (true) {
        emit(block())
        delay(intervalMs)
    }
}
