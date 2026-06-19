package com.droid.riderparadise

import app.cash.turbine.test
import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.domain.repository.OtpDelivery
import com.droid.riderparadise.domain.repository.OtpRepository
import com.droid.riderparadise.domain.repository.OtpResult
import com.droid.riderparadise.feature.auth.AuthEffect
import com.droid.riderparadise.feature.auth.AuthIntent
import com.droid.riderparadise.feature.auth.AuthStep
import com.droid.riderparadise.feature.auth.AuthViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeOtpRepository(
        var requestResult: Resource<OtpDelivery> = Resource.Success(OtpDelivery("123456", true)),
        var verifyResult: Resource<OtpResult> = Resource.Success(OtpResult.Verified("u_1", true)),
    ) : OtpRepository {
        override suspend fun requestOtp(phone: String) = requestResult
        override suspend fun verifyOtp(phone: String, code: String) = verifyResult
    }

    @Test
    fun `invalid phone surfaces error and stays on phone step`() = runTest {
        val vm = AuthViewModel(FakeOtpRepository())
        vm.onIntent(AuthIntent.PhoneChanged("123"))
        vm.onIntent(AuthIntent.SubmitPhone)

        val state = vm.state.value
        assertEquals(AuthStep.PHONE, state.step)
        assertEquals("Enter a valid phone number", state.error)
    }

    @Test
    fun `valid phone advances to otp step`() = runTest {
        val vm = AuthViewModel(FakeOtpRepository())
        vm.onIntent(AuthIntent.PhoneChanged("4158204407"))
        vm.onIntent(AuthIntent.SubmitPhone)

        assertEquals(AuthStep.OTP, vm.state.value.step)
        // Notification delivered → no in-UI fallback code shown.
        assertNull(vm.state.value.devCodeHint)
    }

    @Test
    fun `blocked notification exposes dev code hint`() = runTest {
        val repo = FakeOtpRepository(requestResult = Resource.Success(OtpDelivery("987654", false)))
        val vm = AuthViewModel(repo)
        vm.onIntent(AuthIntent.PhoneChanged("4158204407"))
        vm.onIntent(AuthIntent.SubmitPhone)

        assertEquals("987654", vm.state.value.devCodeHint)
    }

    @Test
    fun `verified new user emits navigate to onboarding`() = runTest {
        val vm = AuthViewModel(FakeOtpRepository())
        vm.onIntent(AuthIntent.PhoneChanged("4158204407"))
        vm.onIntent(AuthIntent.SubmitPhone)
        vm.onIntent(AuthIntent.OtpChanged("123456"))

        vm.effects.test {
            vm.onIntent(AuthIntent.VerifyOtp)
            assertEquals(AuthEffect.NavigateToOnboarding, awaitItem())
        }
    }

    @Test
    fun `verified existing user emits navigate to home`() = runTest {
        val repo = FakeOtpRepository(verifyResult = Resource.Success(OtpResult.Verified("u_1", false)))
        val vm = AuthViewModel(repo)
        vm.onIntent(AuthIntent.PhoneChanged("4158204407"))
        vm.onIntent(AuthIntent.SubmitPhone)
        vm.onIntent(AuthIntent.OtpChanged("123456"))

        vm.effects.test {
            vm.onIntent(AuthIntent.VerifyOtp)
            assertEquals(AuthEffect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `invalid code clears otp and shows attempts left`() = runTest {
        val repo = FakeOtpRepository(verifyResult = Resource.Success(OtpResult.Invalid(3)))
        val vm = AuthViewModel(repo)
        vm.onIntent(AuthIntent.PhoneChanged("4158204407"))
        vm.onIntent(AuthIntent.SubmitPhone)
        vm.onIntent(AuthIntent.OtpChanged("000000"))
        vm.onIntent(AuthIntent.VerifyOtp)

        assertEquals("", vm.state.value.otp)
        assertTrue(vm.state.value.error!!.contains("3 attempts left"))
    }

    @Test
    fun `back to phone resets otp state`() = runTest {
        val vm = AuthViewModel(FakeOtpRepository())
        vm.onIntent(AuthIntent.PhoneChanged("4158204407"))
        vm.onIntent(AuthIntent.SubmitPhone)
        vm.onIntent(AuthIntent.OtpChanged("12"))
        vm.onIntent(AuthIntent.BackToPhone)

        assertEquals(AuthStep.PHONE, vm.state.value.step)
        assertEquals("", vm.state.value.otp)
    }
}
