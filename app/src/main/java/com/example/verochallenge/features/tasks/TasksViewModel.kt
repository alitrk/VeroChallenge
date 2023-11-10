package com.example.verochallenge.features.tasks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verochallenge.data.model.Task
import com.example.verochallenge.data.repo.TaskRepository
import com.example.verochallenge.util.DataStoreRepository
import com.example.verochallenge.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _taskItems = MutableStateFlow<Resource<List<Task>>>(Resource.Loading(emptyList()))
    val taskItems: StateFlow<Resource<List<Task>>> get() = _taskItems
    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()


    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    var pendingScrollToTopAfterRefresh = false

    init {
        authorizeAndGetTaskItems()
    }

    private fun authorizeAndGetTaskItems() {
        viewModelScope.launch {
            try {
                // authorize
                taskRepository.authorize().collect { response ->
                    val accessToken = response.data?.oauth?.accessToken
                    if (accessToken != null) {
                        dataStoreRepository.saveToDataStore(accessToken)
                    }
                    fetchTaskItems()
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    e.message?.let {
                        Log.e(TaskRepository::class.simpleName, it)
                    }
                } else {
                    e.message?.let { Log.e(TaskRepository::class.simpleName, it) }
                    e.printStackTrace()
                }
            }
        }
    }



    private fun fetchTaskItems() {
        viewModelScope.launch {
            taskRepository.getTaskItems(
                accessToken = getAccessToken(),
                onFetchSuccess = {
                    pendingScrollToTopAfterRefresh = true
                },
                onFetchFailed = { t ->
                    viewModelScope.launch { eventChannel.send(Event.ShowErrorMessage(t)) }
                }
            ).collect { resource ->
                _taskItems.value = resource
            }
        }
    }

    private suspend fun getAccessToken(): String {
        return dataStoreRepository.readFromDataStore.first()
    }
    fun onManualRefresh() {
        authorizeAndGetTaskItems()
    }
    enum class Refresh {
        FORCE, NORMAL
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }
}
