package com.example.todolistev.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistev.Utils
import com.example.todolistev.data.model.TaskCategory
import com.example.todolistev.data.model.TaskEntity
import com.example.todolistev.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository, private val context: Context) : ViewModel() {

    lateinit var alarmManager: AlarmManager
    val tasks: Flow<List<TaskEntity>> = repository.getAllTasks()
    val completeTasks: Flow<List<TaskEntity>> = repository.getAllCompleteTasks()
    private val _dataLoading = MutableSharedFlow<Boolean>()
    val dataLoading: SharedFlow<Boolean> = _dataLoading

    fun addTask(taskDescription: String, dueDateInMiles: Long) {
        val task = TaskEntity(
            id = 0,
            taskTitle = "Описание:)",
            taskDescription = taskDescription,
            taskCategory = TaskCategory.PERSONAL,
            taskDueDate = dueDateInMiles,
            isCompleted = false
        )

        val taskDueDate = dueDateInMiles - AlarmManager.INTERVAL_HOUR * 8
//        val taskDueDate = System.currentTimeMillis() + 10 *1000


        showProgress()
        viewModelScope.launch {
            val taskId = repository.insert(task)
            scheduleNotification(taskId.toInt(), taskDueDate, task.taskDescription)
        }
        hideProgress()
    }

    fun updateTask(task: TaskEntity) {

        val taskDueDate = task.taskDueDate - AlarmManager.INTERVAL_HOUR * 8
//        val taskDueDate = System.currentTimeMillis() + 10 *1000

        scheduleNotification(task.id, taskDueDate, task.taskDescription)

        showProgress()
        viewModelScope.launch {
            repository.update(task)
        }
        hideProgress()
    }

    fun toggleCompletionTask(task: TaskEntity) {
        if (task.isCompleted) {
        cancelNotification(task.id, task.taskDescription)
        } else {
            scheduleNotification(task.id, task.taskDueDate, task.taskDescription)
        }

        showProgress()
        viewModelScope.launch {
            repository.update(task)
        }
        hideProgress()
    }

    fun deleteTask(task: TaskEntity) {
        cancelNotification(task.id, task.taskDescription)

        showProgress()
        viewModelScope.launch {
            repository.delete(task)
        }
        hideProgress()
    }

//    suspend fun getAllTasks(): Flow<List<TaskEntity>> {
//        _dataLoading.emit(true)
//        val tasks: Flow<List<TaskEntity>> = repository.getAllTasks()
//        _dataLoading.emit(false)
//        return tasks
//    }

    private fun hideProgress() {
        viewModelScope.launch {
            _dataLoading.emit(false)
        }
    }

    private fun showProgress() {
        viewModelScope.launch {
            _dataLoading.emit(true)
        }
    }

    private fun scheduleNotification(id: Int, date: Long, text: String) {
        val pendingIntent: PendingIntent? = Utils.getPendingIntent(context, id, text)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                date,
                pendingIntent!!
            )
        }
    }

    private fun cancelNotification(id: Int, text: String) {
        val pendingIntent: PendingIntent? = Utils.getPendingIntent(context, id, text)
        alarmManager.cancel(pendingIntent!!)
    }

}