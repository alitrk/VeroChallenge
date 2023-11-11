package com.example.verochallenge.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    val tasks: TaskDto,
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val updatedAt: Long = System.currentTimeMillis()
)