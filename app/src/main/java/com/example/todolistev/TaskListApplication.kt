package com.example.todolistev

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.room.Room
import com.example.todolistev.data.database.TaskDao
import com.example.todolistev.data.database.TaskDatabase
import com.example.todolistev.data.repository.TaskRepositoryImpl
import com.example.todolistev.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers

class TaskListApplication: Application() {
    // Создаем все зависимости в одном месте
    // Порядок важен: сначала низкоуровневые, потом высокоуровневые

    // 1. Создаем DAO (зависимость самого нижнего уровня)
    private lateinit var database: TaskDatabase
    private lateinit var taskDao: TaskDao

    // 2. Создаем Реализацию Репозитория (зависит от DAO)
    private lateinit var taskRepositoryImpl: TaskRepositoryImpl

    // 3. Создаем UseCase'ы или просто прокси-переменную для ViewModel (зависит от userRepositoryImpl)
    lateinit var taskRepository: TaskRepository // Открытый интерфейс

    companion object {
        const val channelName = "Reminders"
        const val channelDescription = "Channel for reminders"
        const val channelId = "reminders"
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    description = channelDescription
                    setSound(Settings.System.DEFAULT_NOTIFICATION_URI, AudioAttributes.Builder().setUsage(
                        AudioAttributes.USAGE_NOTIFICATION). build())
                }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Инициализация
        database = TaskDatabase.getInstance(this)!!
        taskDao = database.taskDao()

        taskRepositoryImpl = TaskRepositoryImpl(taskDao, Dispatchers.IO)
        taskRepository = taskRepositoryImpl // Присваиваем реализацию интерфейсу
    }
}