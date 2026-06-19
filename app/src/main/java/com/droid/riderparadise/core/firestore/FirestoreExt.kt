package com.droid.riderparadise.core.firestore

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Real-time list flow from a Firestore [Query]. Emits on every server/local change.
 * On error (e.g. permission denied / offline with no cache) emits an empty list so the
 * UI degrades gracefully instead of hanging.
 */
fun <T> Query.snapshotListFlow(map: (DocumentSnapshot) -> T?): Flow<List<T>> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            trySend(emptyList())
            return@addSnapshotListener
        }
        val items = snapshot?.documents?.mapNotNull { runCatching { map(it) }.getOrNull() } ?: emptyList()
        trySend(items)
    }
    awaitClose { registration.remove() }
}

/** Real-time single-document flow. Emits null when the doc is missing or on error. */
fun <T> com.google.firebase.firestore.DocumentReference.snapshotFlow(map: (DocumentSnapshot) -> T?): Flow<T?> = callbackFlow {
    val registration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            trySend(null)
            return@addSnapshotListener
        }
        trySend(snapshot?.takeIf { it.exists() }?.let { runCatching { map(it) }.getOrNull() })
    }
    awaitClose { registration.remove() }
}
