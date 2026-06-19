package com.droid.riderparadise.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droid.riderparadise.domain.model.RideParticipant
import com.droid.riderparadise.domain.model.Rider
import com.droid.riderparadise.domain.repository.RiderRepository
import com.droid.riderparadise.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileTabState(
    val user: Rider? = null,
    val legend: List<RideParticipant> = emptyList(),
)

@HiltViewModel
class ProfileTabViewModel @Inject constructor(
    private val userRepository: UserRepository,
    riderRepository: RiderRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileTabState(legend = riderRepository.colorLegend()))
    val state: StateFlow<ProfileTabState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collect { user ->
                _state.update { it.copy(user = user) }
            }
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            userRepository.signOut()
            onDone()
        }
    }
}
