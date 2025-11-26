package com.example.todolistev

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class Utils {
    companion object{
        fun getPendingIntent(context: Context, id: Int, text: String): PendingIntent? {
            val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                putExtra("text", text)
                putExtra("id", id)
            }
            return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }
}