package com.droid.riderparadise.feature.home

import androidx.lifecycle.viewModelScope
import com.droid.riderparadise.core.mvi.MviViewModel
import com.droid.riderparadise.core.mvi.UiEffect
import com.droid.riderparadise.core.mvi.UiIntent
import com.droid.riderparadise.core.mvi.UiState
import com.droid.riderparadise.domain.model.Group
import com.droid.riderparadise.domain.model.Rider
import com.droid.riderparadise.domain.repository.GroupRepository
import com.droid.riderparadise.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val user: Rider? = null,
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = true,
) : UiState

sealed interface HomeIntent : UiIntent {
    data class Join(val groupId: String) : HomeIntent
    data class Request(val groupId: String) : HomeIntent
    data object SignOut : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data object NavigateToAuth : HomeEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    init {
        viewModelScope.launch {
            combine(
                userRepository.observeCurrentUser(),
                groupRepository.observeGroups(),
            ) { user, groups -> user to groups }
                .collect { (user, groups) ->
                    setState { copy(user = user, groups = groups, isLoading = false) }
                }
        }
        viewModelScope.launch { groupRepository.refreshGroups() }
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.Join -> viewModelScope.launch { groupRepository.joinGroup(intent.groupId) }
            is HomeIntent.Request -> viewModelScope.launch { groupRepository.requestToJoin(intent.groupId) }
            HomeIntent.SignOut -> viewModelScope.launch {
                userRepository.signOut()
                emitEffect(HomeEffect.NavigateToAuth)
            }
        }
    }
}
