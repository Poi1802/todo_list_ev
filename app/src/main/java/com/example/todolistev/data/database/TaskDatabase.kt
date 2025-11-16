package com.example.todolistev.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todolistev.data.model.TaskCategory
import com.example.todolistev.data.model.TaskEntity

const val DATABASE_VERSION = 4

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
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return INSTANCE
        }

//        val PREPOPULATE_DATA: List<TaskEntity> = listOf<TaskEntity>(
//            TaskEntity(
//                0,
//                "Simple title1",
//                "Simple task description1",
//                TaskCategory.FITNESS,
//                false
//            ),
//            TaskEntity(
//                0,
//                "Simple title2",
//                "Simple task description2",
//                TaskCategory.FITNESS,
//                false
//            ),
//            TaskEntity(
//                0,
//                "Simple title3",
//                "Simple task description3",
//                TaskCategory.WORK,
//                false
//            ),
//        )
    }
}