package com.droid.riderparadise.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droid.riderparadise.domain.model.Group
import com.droid.riderparadise.domain.model.Ride
import com.droid.riderparadise.domain.model.Rider
import com.droid.riderparadise.domain.model.RsvpResponse
import com.droid.riderparadise.domain.repository.GroupRepository
import com.droid.riderparadise.domain.repository.RideRepository
import com.droid.riderparadise.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupsUiState(
    val user: Rider? = null,
    val groups: List<Group> = emptyList(),
    val rideByGroup: Map<String, Ride> = emptyMap(),
)

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val rideRepository: RideRepository,
    userRepository: UserRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GroupsUiState())
    val state: StateFlow<GroupsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            rideRepository.ensureSeeded()
            groupRepository.refreshGroups()
        }
        viewModelScope.launch {
            combine(
                groupRepository.observeJoinedGroups(),
                rideRepository.observeRides(),
                userRepository.observeCurrentUser(),
            ) { groups, rides, user ->
                Triple(user, groups, rides.groupBy { it.groupId }.mapValues { it.value.first() })
            }.collect { (user, groups, rideByGroup) ->
                _state.update { it.copy(user = user, groups = groups, rideByGroup = rideByGroup) }
            }
        }
    }

    fun setRsvp(rideId: String, response: RsvpResponse) {
        viewModelScope.launch { rideRepository.setRsvp(rideId, response) }
    }
}
