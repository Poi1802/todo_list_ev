package com.example.todolistev

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Receiver triggered")

        try {
            val text = intent?.getStringExtra("text")
            val id = intent?.getIntExtra("id", 0)

            // Start service show notifications

            val serviceIntent = Intent(context, ReminderService::class.java).apply{
                putExtra("text", text)
                putExtra("id", id)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(serviceIntent)
            } else {
                context?.startService(serviceIntent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in receiver: ${e.message}")
        }
    }
}