package com.example.verochallenge.data.repo

import androidx.room.withTransaction
import com.example.verochallenge.BuildConfig
import com.example.verochallenge.data.api.TaskApi
import com.example.verochallenge.data.model.Task
import com.example.verochallenge.data.model.login.LoginRequest
import com.example.verochallenge.data.model.login.LoginResponse
import com.example.verochallenge.local.TaskDatabase
import com.example.verochallenge.util.Resource
import com.example.verochallenge.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskApi: TaskApi,
    private val taskDatabase: TaskDatabase
) {
    private val taskDao = taskDatabase.taskDao()
    suspend fun authorize(): Flow<Resource<LoginResponse>> {
        return flow {
            try {
                val body = LoginRequest(BuildConfig.userName, BuildConfig.password)
                val loginResponse = taskApi.login(
                    TaskApi.authorization,
                    TaskApi.contentType,
                    body
                )
                emit(Resource.Success(loginResponse))
            } catch (e: IOException) {
                emit(Resource.Error(Throwable("Network error: ${e.localizedMessage}")))
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Unauthorized"
                    404 -> "User not found"
                    else -> "Unknown error"
                }
                emit(Resource.Error(Throwable("HTTP error (${e.code()}): $errorMessage")))
            } catch (e: Exception) {
                emit(Resource.Error(e))
            }
        }
    }

    fun getTaskItems(
        accessToken: String,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<Task>>> =
        networkBoundResource(
            query = {
                taskDao.getAllTaskResponses()
            },
            fetch = {
                val taskItems = taskApi.getTaskItems("Bearer $accessToken")
                taskItems
            },
            saveFetchResult = { taskItems ->
                taskDatabase.withTransaction {
                    taskDao.deleteAllTasks()
                    taskDao.insertTaskItems(taskItems.map { taskDto ->
                        Task(tasks = taskDto, id = 0)
                    })
                }
            },
            onFetchSuccess = onFetchSuccess,
            onFetchFailed = { t ->
                if (t !is HttpException && t !is IOException) {
                    throw t
                }
                onFetchFailed(t)
            }
        )
}