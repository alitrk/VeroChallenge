package com.example.verochallenge.data.model.login

import com.google.gson.annotations.SerializedName

data class Oauth(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("expires_in")
    val expiresIn: Int,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("scope")
    val scope: Any,

    @SerializedName("token_type")
    val tokenType: String
)