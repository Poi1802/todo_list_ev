package com.example.todolistev.data.repository

import com.example.todolistev.data.database.TaskDao
import com.example.todolistev.data.model.TaskEntity
import com.example.todolistev.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/*
Сделать интерфейс репозитория
Здесь прокинуть контекст и coroutineDispatcher
init database
 */
class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val backgroundDispatcher: CoroutineDispatcher
): TaskRepository {

    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getTasks()
    }

    override fun getAllCompleteTasks(): Flow<List<TaskEntity>> {
        return taskDao.getCompleteTasks()
    }

    override suspend fun insert(task: TaskEntity): Long {
        val taskId: Long
        withContext(backgroundDispatcher){
            taskId =  taskDao.insertTask(task)
        }
        return taskId
    }

    override suspend fun update(task: TaskEntity) {
        withContext(backgroundDispatcher){
            taskDao.updateTask(task)
        }
    }

    override suspend fun delete(task: TaskEntity) {
        withContext(backgroundDispatcher){
            taskDao.deleteTask(task)
        }
    }
}