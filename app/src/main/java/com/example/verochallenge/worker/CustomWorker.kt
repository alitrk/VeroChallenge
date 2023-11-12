package com.example.verochallenge.worker

/*
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.verochallenge.data.repo.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class CustomWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val WORK_NAME = "custom_work"
    }
    override suspend fun doWork(): Result {
        return try {
            withContext(Dispatchers.IO) {
                // Perform the task retrieval here
                taskRepository.getTaskItems(
                    forceRefresh = true,
                    onFetchSuccess = {
                        // Handle success if needed
                    },
                    onFetchFailed = { t ->
                        // Handle failure if needed
                    }
                ).collect { resource ->
                    // Process the collected resource if needed
                    Log.d("CustomWorker", "${resource.data}")
                }
            }

            Result.success()
        } catch (e: Exception) {
            // Handle exceptions if needed
            Result.failure()
        }
    }

}*/