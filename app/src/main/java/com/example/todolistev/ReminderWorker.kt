package com.example.todolistev

import android.app.PendingIntent
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(
    context,
    params
) {
    override fun doWork(): Result {
        try {
            val text = inputData.getString("text")
            val id = inputData.getInt("id", 0)

            val pendingIntent: PendingIntent = Utils.getPendingIntent(applicationContext, id, text!!)
            pendingIntent.send()

            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}