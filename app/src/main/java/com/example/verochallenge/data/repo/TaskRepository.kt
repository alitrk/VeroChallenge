package com.example.verochallenge.data.repo

import androidx.room.withTransaction
import com.example.verochallenge.data.api.TaskApi
import com.example.verochallenge.data.model.Task
import com.example.verochallenge.data.model.login.LoginRequest
import com.example.verochallenge.data.model.login.LoginResponse
import com.example.verochallenge.local.TaskDatabase
import com.example.verochallenge.util.DataStoreRepository
import com.example.verochallenge.util.Resource
import com.example.verochallenge.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskApi: TaskApi,
    private val taskDatabase: TaskDatabase,
    private val dataStoreRepository: DataStoreRepository
) {
    private val taskDao = taskDatabase.taskDao()
    private suspend fun authorize(): Flow<Resource<LoginResponse>> {
        return flow {
            try {
                val body = LoginRequest("365", "1")
                val loginResponse = taskApi.login(
                    TaskApi.authorization,
                    TaskApi.contentType,
                    body
                )
                saveAccessTokenToDataStore(loginResponse.oauth.accessToken)
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
        forceRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<Task>>> =
        networkBoundResource(
            query = {
                taskDao.getAllTaskResponses()
            },
            fetch = {
                subToAuthorize()
                val accessToken = getAccessToken()
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
            shouldFetch = { fetchedTaskItems ->
                if (forceRefresh) {
                    true
                } else {
                    val sortedTasks = fetchedTaskItems.sortedBy { task ->
                        task.updatedAt
                    }
                    val oldestTimestamp = sortedTasks.firstOrNull()?.updatedAt
                    val needsRefresh = oldestTimestamp == null ||
                            oldestTimestamp < System.currentTimeMillis() -
                            TimeUnit.MINUTES.toMillis(15)

                    needsRefresh
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


    private suspend fun subToAuthorize() {
        authorize().collect{

        }
    }

    private suspend fun getAccessToken(): String {
        return dataStoreRepository.readFromDataStore.first()
    }

    private suspend fun saveAccessTokenToDataStore(accessToken: String) {
        dataStoreRepository.saveToDataStore(accessToken)
    }

}