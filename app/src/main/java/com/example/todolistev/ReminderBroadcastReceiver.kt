package com.example.todolistev

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val appIntent = Intent(context, MainActivity::class.java)


            val text = intent?.getStringExtra("text") ?: "Reminder text"
            val id = intent?.getIntExtra("id", 0)

            val pendingTransitionIntent = PendingIntent.getActivity(
                context,
                id!!,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notificationManager = NotificationManagerCompat.from(context)
            val builder = NotificationCompat.Builder(context, TaskListApplication.channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Задача на сегодня")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingTransitionIntent)

            notificationManager.notify(id, builder.build())
        }
    }
}