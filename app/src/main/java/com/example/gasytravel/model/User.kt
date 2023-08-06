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
    @SerializedName("id")
    @Expose
    val id: String,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("email")
    @Expose
    val email: String,

    @SerializedName("userType")
    @Expose
    val userType: String,

    @SerializedName("deviceToken")
    @Expose
    var deviceToken: String
)

data class LoginResponseModel(
    @SerializedName("token")
    @Expose
    val token: String,

    @SerializedName("user")
    @Expose
    val user: UserModel,
)

data class SignUpModel(
    val name: String,
    val email: String,
    val password: String
)

data class SignUpResponseModel(
    @SerializedName("success")
    @Expose
    val success: Boolean,

    @SerializedName("message")
    @Expose
    val message: String?
)