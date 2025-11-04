package com.example.todolistev.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todolistev.data.model.TaskEntity

const val DATABASE_VERSION = 1

@Database(entities = [TaskEntity::class], version = DATABASE_VERSION, exportSchema = true)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private var INSTANCE: TaskDatabase? = null;

        @Synchronized
        fun getInstance(context: Context): TaskDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "todo_database"
                ).build()
            }

            return INSTANCE
        }
    }
}