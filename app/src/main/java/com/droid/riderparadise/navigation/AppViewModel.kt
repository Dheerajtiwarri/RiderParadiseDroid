package com.droid.riderparadise.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droid.riderparadise.data.datastore.SessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Decides the initial destination based on persisted session/onboarding state. */
@HiltViewModel
class AppViewModel @Inject constructor(
    private val session: SessionStore,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = session.userId()
            val onboarded = session.isOnboarded()
            _startDestination.value = when {
                userId == null -> Routes.AUTH
                !onboarded -> Routes.ONBOARDING_PROFILE
                else -> Routes.MAIN
            }
        }
    }
}
