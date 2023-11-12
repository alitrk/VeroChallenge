package com.example.verochallenge

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application()/*, Configuration.Provider {

    @Inject
    lateinit var workerFactory: CustomWorkerFactory
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

}
class CustomWorkerFactory @Inject constructor(private val taskRepository: TaskRepository) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker =
        CustomWorker(appContext, workerParameters, taskRepository)

}*/