package com.example.verochallenge.features.tasks

import androidx.lifecycle.ViewModel
import com.example.verochallenge.data.repo.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {
}