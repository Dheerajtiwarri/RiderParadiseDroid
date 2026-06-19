package com.droid.riderparadise.feature.ride

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droid.riderparadise.domain.model.ChatKind
import com.droid.riderparadise.domain.model.ChatMessage
import com.droid.riderparadise.domain.model.Ride
import com.droid.riderparadise.domain.model.RideParticipant
import com.droid.riderparadise.domain.model.RideRecap
import com.droid.riderparadise.domain.repository.RideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanRideViewModel @Inject constructor(
    private val rideRepository: RideRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val groupId: String = savedStateHandle["groupId"] ?: ""
    val groupName: String = savedStateHandle["groupName"] ?: ""

    private val _title = MutableStateFlow("Sunday Hills Loop")
    val title: StateFlow<String> = _title.asStateFlow()
    private val _difficulty = MutableStateFlow("Moderate")
    val difficulty: StateFlow<String> = _difficulty.asStateFlow()
    private val _publishing = MutableStateFlow(false)
    val publishing: StateFlow<Boolean> = _publishing.asStateFlow()

    fun onTitleChange(v: String) { _title.value = v }
    fun onDifficulty(v: String) { _difficulty.value = v }

    fun publish(onPublished: (String) -> Unit) {
        viewModelScope.launch {
            _publishing.value = true
            val id = rideRepository.createRide(groupId, groupName, _title.value, _difficulty.value)
            _publishing.value = false
            onPublished(id)
        }
    }
}

@HiltViewModel
class LobbyViewModel @Inject constructor(
    private val rideRepository: RideRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val rideId: String = savedStateHandle["rideId"] ?: ""
    val ride: StateFlow<Ride?> =
        rideRepository.observeRide(rideId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val roster: List<RideParticipant> = rideRepository.roster(rideId)

    fun start(onStarted: (String) -> Unit) {
        viewModelScope.launch {
            rideRepository.startRide(rideId)
            onStarted(rideId)
        }
    }
}

@HiltViewModel
class LiveRideViewModel @Inject constructor(
    private val rideRepository: RideRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val rideId: String = savedStateHandle["rideId"] ?: ""
    val ride: StateFlow<Ride?> =
        rideRepository.observeRide(rideId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val roster: List<RideParticipant> = rideRepository.roster(rideId)

    init {
        viewModelScope.launch { rideRepository.startRide(rideId) }
    }

    fun endRide(onEnded: (String) -> Unit) {
        viewModelScope.launch {
            rideRepository.completeRide(rideId)
            onEnded(rideId)
        }
    }
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val rideRepository: RideRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val rideId: String = savedStateHandle["rideId"] ?: ""
    val messages: StateFlow<List<ChatMessage>> =
        rideRepository.observeMessages(rideId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _draft = MutableStateFlow("")
    val draft: StateFlow<String> = _draft.asStateFlow()
    fun onDraft(v: String) { _draft.value = v }

    fun send() {
        val text = _draft.value
        _draft.value = ""
        viewModelScope.launch { rideRepository.sendMessage(rideId, text, ChatKind.NORMAL) }
    }

    fun sendMacro(text: String) {
        viewModelScope.launch { rideRepository.sendMessage(rideId, text, ChatKind.MACRO) }
    }
}

@HiltViewModel
class RecapViewModel @Inject constructor(
    rideRepository: RideRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val rideId: String = savedStateHandle["rideId"] ?: ""
    val recap: RideRecap = rideRepository.recapFor(rideId)
}
