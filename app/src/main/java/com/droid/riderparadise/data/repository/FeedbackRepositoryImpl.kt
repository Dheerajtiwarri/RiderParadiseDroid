package com.droid.riderparadise.data.repository

import com.droid.riderparadise.core.network.FeedbackBody
import com.droid.riderparadise.core.network.RiderParadiseApi
import com.droid.riderparadise.core.result.Resource
import com.droid.riderparadise.domain.model.Feedback
import com.droid.riderparadise.domain.repository.FeedbackRepository
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(
    private val api: RiderParadiseApi,
) : FeedbackRepository {

    override suspend fun submit(feedback: Feedback): Resource<Unit> = try {
        val env = api.feedback(
            FeedbackBody(
                type = feedback.type.name,
                area = feedback.area,
                body = feedback.body,
                appVersion = feedback.appVersion,
            )
        )
        if (env.error == null) Resource.Success(Unit)
        else Resource.Error(env.error.message ?: "Could not send feedback")
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Could not send feedback", e)
    }
}
