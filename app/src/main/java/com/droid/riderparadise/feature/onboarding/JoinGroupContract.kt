package com.droid.riderparadise.feature.onboarding

import com.droid.riderparadise.core.mvi.UiEffect
import com.droid.riderparadise.core.mvi.UiIntent
import com.droid.riderparadise.core.mvi.UiState
import com.droid.riderparadise.domain.model.Group

data class JoinGroupState(
    val groups: List<Group> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
) : UiState {
    val hasJoinedAny: Boolean
        get() = groups.any { it.membership == com.droid.riderparadise.domain.model.MembershipStatus.JOINED }
}

sealed interface JoinGroupIntent : UiIntent {
    data class Join(val groupId: String) : JoinGroupIntent
    data class Request(val groupId: String) : JoinGroupIntent
    data object Continue : JoinGroupIntent
}

sealed interface JoinGroupEffect : UiEffect {
    data object NavigateToHome : JoinGroupEffect
}
