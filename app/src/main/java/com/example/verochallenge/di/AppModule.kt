package com.example.verochallenge.di

import android.content.Context
import androidx.room.Room
import com.example.verochallenge.data.api.TaskApi
import com.example.verochallenge.data.datasource.TaskDataSource
import com.example.verochallenge.data.repo.TaskRepository
import com.example.verochallenge.local.TaskDao
import com.example.verochallenge.local.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFoodsRepository(tds: TaskDataSource): TaskRepository {
        return TaskRepository(tds)
    }

    @Provides
    @Singleton
    fun provideFoodsDataSource(tdao: TaskDao): TaskDataSource {
        return TaskDataSource(tdao)
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(TaskApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): TaskApi =
        retrofit.create(TaskApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): TaskDatabase =
        Room.databaseBuilder(context, TaskDatabase::class.java, "weather_database")
            .fallbackToDestructiveMigration()
            .build()
}