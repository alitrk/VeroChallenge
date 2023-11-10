package com.example.verochallenge.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.verochallenge.data.model.Task

@Database(entities = [Task::class], version = 1)
@TypeConverters(TaskTypeConverter::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}