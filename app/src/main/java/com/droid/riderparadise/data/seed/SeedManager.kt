package com.droid.riderparadise.data.seed

import com.droid.riderparadise.domain.repository.GroupRepository
import com.droid.riderparadise.domain.repository.RideRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeedManager @Inject constructor(
    private val groupRepository: GroupRepository,
    private val rideRepository: RideRepository,
) {
    /** Best-effort: never throws. Firestore denial/offline must not crash app start. */
    suspend fun seed() {
        runCatching { groupRepository.refreshGroups() }
        runCatching { rideRepository.ensureSeeded() }
    }
}
