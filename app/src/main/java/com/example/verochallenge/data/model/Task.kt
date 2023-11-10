package com.example.verochallenge.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tasks")
data class Task(
    val tasks: TaskDto,
    @PrimaryKey(autoGenerate = true)
    val id: Int
) : Serializable