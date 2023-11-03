package com.example.verochallenge.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.verochallenge.data.model.Task

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}