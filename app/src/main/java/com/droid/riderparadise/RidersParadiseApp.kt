package com.droid.riderparadise

import android.app.Application
import com.droid.riderparadise.core.notification.OtpNotifier
import com.droid.riderparadise.data.seed.SeedManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class RidersParadiseApp : Application() {

    @Inject lateinit var otpNotifier: OtpNotifier
    @Inject lateinit var seedManager: SeedManager

    // Backstop so a Firestore denial/offline during seeding can never crash the app.
    private val appScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }
    )

    override fun onCreate() {
        super.onCreate()
        otpNotifier.ensureChannel()
        appScope.launch { seedManager.seed() }
    }
}
