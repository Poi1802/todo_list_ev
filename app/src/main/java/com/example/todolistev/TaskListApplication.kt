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

    override fun onCreate() {
        super.onCreate()

        // Инициализация
        database = TaskDatabase.getInstance(this)!!
        taskDao = database.taskDao()

        taskRepositoryImpl = TaskRepositoryImpl(taskDao, Dispatchers.IO)
        taskRepository = taskRepositoryImpl // Присваиваем реализацию интерфейсу
    }
}