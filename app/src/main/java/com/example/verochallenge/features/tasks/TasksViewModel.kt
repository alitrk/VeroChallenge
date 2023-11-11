package com.example.verochallenge.features.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.verochallenge.data.model.Task
import com.example.verochallenge.data.repo.TaskRepository
import com.example.verochallenge.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _taskItems = MutableStateFlow<Resource<List<Task>>>(Resource.Loading(emptyList()))
    val taskItems: StateFlow<Resource<List<Task>>> get() = _taskItems
    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    var pendingScrollToTopAfterRefresh = false


    init {
        fetchTaskItems(false)
    }
    private fun fetchTaskItems(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            taskRepository.getTaskItems(
                forceRefresh = forceRefresh,
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



    fun onManualRefresh() {
        if (taskItems.value !is Resource.Loading) {
            viewModelScope.launch {
                fetchTaskItems(true)

            }
        }
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }
}
