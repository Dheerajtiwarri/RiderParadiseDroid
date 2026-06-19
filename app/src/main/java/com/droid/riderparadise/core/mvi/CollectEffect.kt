package com.droid.riderparadise.core.mvi

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/** Collects one-shot [UiEffect]s safely while the screen is at least STARTED. */
@Composable
fun <E : UiEffect> CollectEffect(effects: Flow<E>, onEffect: (E) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(effects, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            effects.collect(onEffect)
        }
    }
}

/** Convenience to observe a [MviViewModel]'s state as Compose state. */
@Composable
fun <S : UiState> MviViewModel<S, *, *>.stateAsState(): State<S> =
    state.collectAsStateWithLifecycle()
