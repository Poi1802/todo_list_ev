package com.example.todolistev.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolistev.ReminderScheduler
import com.example.todolistev.domain.repository.TaskRepository

class TaskViewModelFactory(
    private val context: Context,
    private val repository: TaskRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        val reminderScheduler = ReminderScheduler(context)
        return TaskViewModel(repository, reminderScheduler) as T
    }
}