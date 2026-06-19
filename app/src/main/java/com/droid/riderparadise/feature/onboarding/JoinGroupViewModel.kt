package com.droid.riderparadise.feature.onboarding

import androidx.lifecycle.viewModelScope
import com.droid.riderparadise.core.mvi.MviViewModel
import com.droid.riderparadise.domain.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinGroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
) : MviViewModel<JoinGroupState, JoinGroupIntent, JoinGroupEffect>(JoinGroupState()) {

    init {
        observeGroups()
        refresh()
    }

    override fun handleIntent(intent: JoinGroupIntent) {
        when (intent) {
            is JoinGroupIntent.Join -> viewModelScope.launch { groupRepository.joinGroup(intent.groupId) }
            is JoinGroupIntent.Request -> viewModelScope.launch { groupRepository.requestToJoin(intent.groupId) }
            JoinGroupIntent.Continue -> emitEffect(JoinGroupEffect.NavigateToHome)
        }
    }

    private fun observeGroups() {
        viewModelScope.launch {
            groupRepository.observeGroups().collect { groups ->
                setState { copy(groups = groups, isLoading = false) }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch { groupRepository.refreshGroups() }
    }
}
