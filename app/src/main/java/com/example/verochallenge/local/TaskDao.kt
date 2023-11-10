package com.example.verochallenge.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.verochallenge.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItems(task: List<Task>)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM tasks")
    fun getAllTaskResponses(): Flow<List<Task>>
}