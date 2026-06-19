package com.droid.riderparadise.feature.auth

import com.droid.riderparadise.core.mvi.UiEffect
import com.droid.riderparadise.core.mvi.UiIntent
import com.droid.riderparadise.core.mvi.UiState

enum class AuthStep { PHONE, OTP }

data class AuthState(
    val step: AuthStep = AuthStep.PHONE,
    val phone: String = "",
    val otp: String = "",
    val isSubmitting: Boolean = false,
    val resendSeconds: Int = 0,
    val error: String? = null,
    /** Shown in-UI only when the OTP notification could not be posted (permission denied). */
    val devCodeHint: String? = null,
) : UiState {
    val phoneValid: Boolean get() = phone.filter { it.isDigit() }.length in 7..15
    val otpComplete: Boolean get() = otp.length == 6
    val canResend: Boolean get() = resendSeconds == 0
}

sealed interface AuthIntent : UiIntent {
    data class PhoneChanged(val value: String) : AuthIntent
    data object SubmitPhone : AuthIntent
    data class OtpChanged(val value: String) : AuthIntent
    data object VerifyOtp : AuthIntent
    data object Resend : AuthIntent
    data object BackToPhone : AuthIntent
    data object DismissError : AuthIntent
}

sealed interface AuthEffect : UiEffect {
    data object NavigateToOnboarding : AuthEffect
    data object NavigateToHome : AuthEffect
}
