package com.example.gasytravel.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("email")
    @Expose
    val email: String?,
    @SerializedName("password")
    @Expose
    val password: String?
)

data class UserModel(
    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("email")
    @Expose
    val email: String,

    @SerializedName("userType")
    @Expose
    val userType: String,
)

data class LoginResponseModel(
    @SerializedName("token")
    @Expose
    val token: String,

    @SerializedName("user")
    @Expose
    val user: UserModel,
)