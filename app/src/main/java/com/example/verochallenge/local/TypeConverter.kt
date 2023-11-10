package com.example.verochallenge.local

import androidx.room.TypeConverter
import com.example.verochallenge.data.model.TaskDto
import com.google.gson.Gson

class TaskTypeConverter {
    @TypeConverter
    fun fromTaskDto(taskDto: TaskDto): String {
        // Convert TaskDto to a JSON string
        return Gson().toJson(taskDto)
    }

    @TypeConverter
    fun toTaskDto(taskDtoString: String): TaskDto {
        // Convert JSON string back to TaskDto
        return Gson().fromJson(taskDtoString, TaskDto::class.java)
    }
}