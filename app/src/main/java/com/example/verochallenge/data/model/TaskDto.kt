package com.example.verochallenge.data.model

data class TaskDto(
    val businessUnitKey: String,
    val businessUnit: String,
    val colorCode: String,
    val description: String,
    val isAvailableInTimeTrackingKioskMode: Boolean,
    val parentTaskID: String,
    val prePlanningBoardQuickSelect: Any,
    val sort: String,
    val task: String,
    val title: String,
    val wageType: String,
    val workingTime: Any
)