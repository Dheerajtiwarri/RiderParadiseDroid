package com.droid.riderparadise.feature.auth

import androidx.lifecycle.viewModelScope
import com.droid.riderparadise.core.mvi.MviViewModel
import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.domain.repository.OtpRepository
import com.droid.riderparadise.domain.repository.OtpResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val otpRepository: OtpRepository,
) : MviViewModel<AuthState, AuthIntent, AuthEffect>(AuthState()) {

    private var timerJob: Job? = null

    override fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.PhoneChanged -> setState { copy(phone = intent.value, error = null) }
            is AuthIntent.OtpChanged -> setState { copy(otp = intent.value, error = null) }
            AuthIntent.SubmitPhone -> submitPhone()
            AuthIntent.VerifyOtp -> verifyOtp()
            AuthIntent.Resend -> requestCode(resend = true)
            AuthIntent.BackToPhone -> {
                timerJob?.cancel()
                setState { copy(step = AuthStep.PHONE, otp = "", error = null, devCodeHint = null) }
            }
            AuthIntent.DismissError -> setState { copy(error = null) }
        }
    }

    private fun submitPhone() {
        if (!currentState.phoneValid) {
            setState { copy(error = "Enter a valid phone number") }
            return
        }
        requestCode(resend = false)
    }

    private fun requestCode(resend: Boolean) {
        viewModelScope.launch {
            setState { copy(isSubmitting = true, error = null) }
            when (val result = otpRepository.requestOtp(currentState.phone)) {
                is Resource.Success -> {
                    val delivery = result.data
                    // If the OS blocks notifications, surface the code in-UI so the POC stays usable.
                    setState {
                        copy(
                            step = AuthStep.OTP,
                            isSubmitting = false,
                            otp = if (resend) "" else otp,
                            devCodeHint = if (delivery.deliveredAsNotification) null else delivery.code,
                        )
                    }
                    startResendTimer()
                }
                is Resource.Error -> setState {
                    copy(isSubmitting = false, error = result.message)
                }
            }
        }
    }

    private fun verifyOtp() {
        if (!currentState.otpComplete) return
        viewModelScope.launch {
            setState { copy(isSubmitting = true, error = null) }
            when (val result = otpRepository.verifyOtp(currentState.phone, currentState.otp)) {
                is Resource.Success -> when (val r = result.data) {
                    is OtpResult.Verified -> {
                        setState { copy(isSubmitting = false) }
                        if (r.isNewUser) emitEffect(AuthEffect.NavigateToOnboarding)
                        else emitEffect(AuthEffect.NavigateToHome)
                    }
                    is OtpResult.Invalid -> setState {
                        copy(
                            isSubmitting = false,
                            otp = "",
                            error = "Incorrect code · ${r.attemptsLeft} attempts left",
                        )
                    }
                    OtpResult.Expired -> setState {
                        copy(isSubmitting = false, otp = "", error = "Code expired · resend a new one")
                    }
                }
                is Resource.Error -> setState {
                    copy(isSubmitting = false, error = result.message)
                }
            }
        }
    }

    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            setState { copy(resendSeconds = RESEND_SECONDS) }
            while (currentState.resendSeconds > 0) {
                delay(1000)
                setState { copy(resendSeconds = resendSeconds - 1) }
            }
        }
    }

    companion object {
        private const val RESEND_SECONDS = 24
    }
}
