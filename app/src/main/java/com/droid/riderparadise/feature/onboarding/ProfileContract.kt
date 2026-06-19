package com.droid.riderparadise.feature.onboarding

import com.droid.riderparadise.core.mvi.UiEffect
import com.droid.riderparadise.core.mvi.UiIntent
import com.droid.riderparadise.core.mvi.UiState
import com.droid.riderparadise.domain.model.BikeType

data class ProfileState(
    val displayName: String = "",
    val bikeType: BikeType? = null,
    val shareLocation: Boolean = true,
    val allowContacts: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
) : UiState {
    val canContinue: Boolean get() = displayName.isNotBlank() && bikeType != null
}

sealed interface ProfileIntent : UiIntent {
    data class NameChanged(val value: String) : ProfileIntent
    data class BikeSelected(val type: BikeType) : ProfileIntent
    data class ShareLocationToggled(val value: Boolean) : ProfileIntent
    data class ContactsToggled(val value: Boolean) : ProfileIntent
    data object Continue : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data object NavigateToJoin : ProfileEffect
}
