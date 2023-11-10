package com.example.verochallenge.data.api

import com.example.verochallenge.data.model.TaskDto
import com.example.verochallenge.data.model.login.LoginRequest
import com.example.verochallenge.data.model.login.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface TaskApi {

    companion object {
        const val BASE_URL = "https://api.baubuddy.de/dev/index.php/v1/"
        const val authorization = "Basic QVBJX0V4cGxvcmVyOjEyMzQ1NmlzQUxhbWVQYXNz"
        const val contentType = "application/json"
    }
    @POST("login")
    suspend fun login(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String,
        @Body loginRequest: LoginRequest
    ): LoginResponse
    @GET("tasks/select")
    suspend fun getTaskItems(
        @Header("Authorization") authorization: String,
    ): List<TaskDto>

}


