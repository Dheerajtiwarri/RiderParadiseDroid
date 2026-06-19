package com.droid.riderparadise.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.droid.riderparadise.R
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Delivers the OTP as a local system notification (POC stand-in for SMS).
 * The code is generated and verified entirely on-device.
 */
@Singleton
class OtpNotifier @Inject constructor(
    private val context: Context,
) {
    fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Verification codes",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply { description = "One-time passcodes for signing in" }
            val mgr = context.getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(channel)
        }
    }

    /** Returns false when notifications are blocked (caller should surface the code in-UI as fallback). */
    fun postOtp(code: String): Boolean {
        if (!hasPermission()) return false
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("RidersParadise verification code")
            .setContentText("Your code is $code — expires in 5 minutes.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Your code is $code. It expires in 5 minutes. Don't share it with anyone."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        return true
    }

    private fun hasPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val CHANNEL_ID = "otp"
        private const val NOTIFICATION_ID = 4815
    }
}
