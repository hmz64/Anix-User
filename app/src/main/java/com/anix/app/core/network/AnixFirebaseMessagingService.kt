package com.anix.app.core.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anix.app.core.di.ServiceLocator
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnixFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onNewToken(token: String) {
        Log.d("AnixFCM", "Token refreshed: $token")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ServiceLocator.getNotificationRepository().upsertToken(token)
            } catch (_: Exception) { }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "Anix"
        val body = message.notification?.body ?: message.data["body"] ?: ""

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID++, builder.build())
        } catch (_: SecurityException) {
            Log.w("AnixFCM", "Missing POST_NOTIFICATIONS permission")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Anix",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Anix notifications" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "anix_notifications"
        private var NOTIFICATION_ID = 1000
    }
}
