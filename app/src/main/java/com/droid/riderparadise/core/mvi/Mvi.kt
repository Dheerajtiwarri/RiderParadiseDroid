package com.droid.riderparadise.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Marker for an immutable UI state snapshot rendered by a screen. */
interface UiState

/** Marker for a user/system action fed into a [MviViewModel]. */
interface UiIntent

/** Marker for a one-shot side effect (navigation, toast, notification). */
interface UiEffect

/**
 * Base MVI ViewModel: a single [state] StateFlow is the source of UI truth and a [effects] stream
 * carries one-shot events. Subclasses reduce intents in [handleIntent].
 */
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    val currentState: S get() = _state.value

    fun onIntent(intent: I) = handleIntent(intent)

    protected abstract fun handleIntent(intent: I)

    protected fun setState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    protected fun emitEffect(effect: E) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
