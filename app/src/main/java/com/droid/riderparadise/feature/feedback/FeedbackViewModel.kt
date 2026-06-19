package com.droid.riderparadise.feature.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.domain.model.Feedback
import com.droid.riderparadise.domain.model.FeedbackType
import com.droid.riderparadise.domain.repository.FeedbackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedbackUiState(
    val type: FeedbackType = FeedbackType.SUGGESTION,
    val area: String = "Live map",
    val body: String = "",
    val sending: Boolean = false,
    val sent: Boolean = false,
) {
    val canSend: Boolean get() = body.isNotBlank()
}

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FeedbackUiState())
    val state: StateFlow<FeedbackUiState> = _state.asStateFlow()

    val areas = listOf("Live map", "Navigation", "Chat", "Groups", "Other")

    fun setType(t: FeedbackType) = _state.update { it.copy(type = t) }
    fun setArea(a: String) = _state.update { it.copy(area = a) }
    fun setBody(b: String) = _state.update { it.copy(body = b) }

    fun submit(onSent: () -> Unit) {
        val s = _state.value
        if (!s.canSend) return
        viewModelScope.launch {
            _state.update { it.copy(sending = true) }
            feedbackRepository.submit(Feedback(s.type, s.area, s.body.trim(), "1.0"))
            _state.update { it.copy(sending = false, sent = true) }
            onSent()
        }
    }
}
