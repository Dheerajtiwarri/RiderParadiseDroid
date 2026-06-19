package com.droid.riderparadise.feature.onboarding

import androidx.lifecycle.viewModelScope
import com.droid.riderparadise.core.mvi.MviViewModel
import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : MviViewModel<ProfileState, ProfileIntent, ProfileEffect>(ProfileState()) {

    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.NameChanged -> setState { copy(displayName = intent.value, error = null) }
            is ProfileIntent.BikeSelected -> setState { copy(bikeType = intent.type, error = null) }
            is ProfileIntent.ShareLocationToggled -> setState { copy(shareLocation = intent.value) }
            is ProfileIntent.ContactsToggled -> setState { copy(allowContacts = intent.value) }
            ProfileIntent.Continue -> save()
        }
    }

    private fun save() {
        val bike = currentState.bikeType ?: return
        viewModelScope.launch {
            setState { copy(isSaving = true, error = null) }
            when (val result = userRepository.saveProfile(
                displayName = currentState.displayName.trim(),
                bikeType = bike,
                shareLocation = currentState.shareLocation,
                allowContacts = currentState.allowContacts,
            )) {
                is Resource.Success -> {
                    setState { copy(isSaving = false) }
                    emitEffect(ProfileEffect.NavigateToJoin)
                }
                is Resource.Error -> setState { copy(isSaving = false, error = result.message) }
            }
        }
    }
}
