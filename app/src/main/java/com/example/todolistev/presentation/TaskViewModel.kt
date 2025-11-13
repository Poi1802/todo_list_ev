package com.example.todolistev.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistev.data.database.TaskDatabase
import com.example.todolistev.data.model.TaskCategory
import com.example.todolistev.data.model.TaskEntity
import com.example.todolistev.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks: Flow<List<TaskEntity>> = repository.getAllTasks()
    val completeTasks: Flow<List<TaskEntity>> = repository.getAllCompleteTasks()
    private val _dataLoading = MutableSharedFlow<Boolean>()
    val dataLoading: SharedFlow<Boolean> = _dataLoading

    fun addTask(taskDescription: String) {
        val task = TaskEntity(
            id = 0,
            taskTitle = "Описание:)",
            taskDescription = taskDescription,
            taskCategory = TaskCategory.PERSONAL,
            isCompleted = false
        )

        showProgress()
        viewModelScope.launch {
            repository.insert(task)
        }
        hideProgress()
    }

    fun updateTask(task: TaskEntity) {
        showProgress()
        viewModelScope.launch {
            repository.update(task)
        }
        hideProgress()
    }

    fun toggleCompletionTask(task: TaskEntity) {
        showProgress()
        viewModelScope.launch {
            repository.update(task)
        }
        hideProgress()
    }

    fun deleteTask(task: TaskEntity) {
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


}