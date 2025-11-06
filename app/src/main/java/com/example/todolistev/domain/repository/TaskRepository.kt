package com.example.todolistev.domain.repository

import com.example.todolistev.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun insert(task: TaskEntity)
    suspend fun update(task: TaskEntity)
    suspend fun delete(task: TaskEntity)
}