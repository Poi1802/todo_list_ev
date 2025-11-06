package com.example.todolistev.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolistev.domain.repository.TaskRepository

class TaskViewModelFactory(
    private val repository: TaskRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TaskViewModel(repository) as T
    }
}