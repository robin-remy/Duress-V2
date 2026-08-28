package com.duressvault.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class StealthService : Service() {
    override fun onCreate() {
        super.onCreate()
        val channelId = "duress_stealth"
        val channel = NotificationChannel(channelId, "Sistema", NotificationManager.IMPORTANCE_MIN)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sistema")
            .setContentText("")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
