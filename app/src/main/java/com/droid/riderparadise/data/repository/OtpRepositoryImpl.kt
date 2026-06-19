package com.droid.riderparadise.data.repository

import com.droid.riderparadise.core.network.RequestOtpBody
import com.droid.riderparadise.core.network.RiderParadiseApi
import com.droid.riderparadise.core.network.VerifyOtpBody
import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.data.datastore.SessionStore
import com.droid.riderparadise.domain.repository.OtpDelivery
import com.droid.riderparadise.domain.repository.OtpRepository
import com.droid.riderparadise.domain.repository.OtpResult
import retrofit2.HttpException
import javax.inject.Inject

/**
 * OTP auth backed by the backend (BACKEND.md §4). The server generates, stores and delivers
 * the code; on verification it mints a bearer token which we persist for subsequent calls.
 */
class OtpRepositoryImpl @Inject constructor(
    private val api: RiderParadiseApi,
    private val session: SessionStore,
) : OtpRepository {

    override suspend fun requestOtp(phone: String): Resource<OtpDelivery> = try {
        val env = api.requestOtp(RequestOtpBody(phone))
        if (env.error != null) {
            Resource.Error(env.error.message ?: "Could not start verification")
        } else {
            // Server owns delivery (SMS / notification); the code is never returned to the client.
            Resource.Success(OtpDelivery(code = "", deliveredAsNotification = true))
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Could not start verification", e)
    }

    override suspend fun verifyOtp(phone: String, code: String): Resource<OtpResult> = try {
        val env = api.verifyOtp(VerifyOtpBody(phone, code))
        val auth = env.data
        if (auth != null) {
            session.setToken(auth.token)
            val userId = auth.user?.id
            if (userId != null) session.setUserId(userId)
            val isNewUser = auth.user?.displayName.isNullOrBlank()
            Resource.Success(OtpResult.Verified(userId ?: "", isNewUser))
        } else {
            Resource.Success(mapVerifyError(env.error?.code))
        }
    } catch (e: HttpException) {
        // Backend signals invalid/expired code via 4xx; degrade to a typed result.
        Resource.Success(mapVerifyError(e.code().toString()))
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Verification failed", e)
    }

    private fun mapVerifyError(code: String?): OtpResult =
        if (code?.contains("EXPIRED", ignoreCase = true) == true) OtpResult.Expired
        else OtpResult.Invalid(attemptsLeft = 0)
}
