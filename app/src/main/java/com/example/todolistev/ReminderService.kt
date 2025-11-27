package com.example.todolistev

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat

class ReminderService : Service() {

    companion object {
        const val TAG = "ReminderService"
        const val NOTIFICATION_ID = 1802
        const val CHANNEL_ID = "reminders"
        const val CHANNEL_NAME = "Reminders"
        const val CHANNEL_DESCRIPTION = "Channel for reminders"
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "Reminder service started")

        //Создаем уведомления для foreground service
        startForeground(NOTIFICATION_ID, createForegroundNotification())

        try {
            val text = intent?.getStringExtra("text") ?: "Notifications"
            val id = intent?.getIntExtra("id", 0) ?: 0

            showNotification(text, id)
            Log.d(TAG, "Notification shown $text")
        } catch(e: Exception) {
            Log.e(TAG, "Service error: ${e.message}")
        } finally {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun showNotification(text: String, id: Int) {
        createNotificationChannel()

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val appIntent = Intent(this, MainActivity::class.java)

        val pendingTransitionIntent = PendingIntent.getActivity(
            this,
            id,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = NotificationManagerCompat.from(this)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Задача на сегодня")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingTransitionIntent)

        notificationManager.notify(id, notification.build())

    }

    private fun createForegroundNotification(): Notification {
        createNotificationChannel()

        val notification =  NotificationCompat.Builder(this, "reminder_channel")
            .setContentTitle("Reminder service")
            .setContentText("Show notifications")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // Для Android 14+ устанавливаем тип сервиса
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Используем тип "shortService" для кратковременных операций
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE,
                // Дополнительные типы если нужно:
                // ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        return notification
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    description = CHANNEL_DESCRIPTION
                    setSound(
                        Settings.System.DEFAULT_NOTIFICATION_URI,
                        AudioAttributes.Builder().setUsage(
                            AudioAttributes.USAGE_NOTIFICATION
                        ).build()
                    )
                    enableVibration(true)
                    setShowBadge(true)
                }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onBind(intent: Intent?): IBinder? = null
}

