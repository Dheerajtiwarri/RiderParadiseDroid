package com.droid.riderparadise.feature.riders

import androidx.lifecycle.ViewModel
import com.droid.riderparadise.domain.model.RiderContact
import com.droid.riderparadise.domain.repository.RiderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class RidersUiState(
    val onApp: List<RiderContact> = emptyList(),
    val toInvite: List<RiderContact> = emptyList(),
    val suggested: List<RiderContact> = emptyList(),
    val invited: Set<String> = emptySet(),
)

@HiltViewModel
class RidersViewModel @Inject constructor(
    riderRepository: RiderRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        RidersUiState(
            onApp = riderRepository.ridersOnApp(),
            toInvite = riderRepository.ridersToInvite(),
            suggested = riderRepository.suggestedRiders(),
        )
    )
    val state: StateFlow<RidersUiState> = _state.asStateFlow()

    fun toggleFollow(id: String) {
        _state.update { s ->
            s.copy(
                onApp = s.onApp.map { if (it.id == id) it.copy(following = !it.following) else it },
                suggested = s.suggested.map { if (it.id == id) it.copy(following = !it.following) else it },
            )
        }
    }

    fun invite(id: String) {
        _state.update { it.copy(invited = it.invited + id) }
    }
}
