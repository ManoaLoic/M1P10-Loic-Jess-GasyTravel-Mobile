package com.example.gasytravel.service

import com.example.gasytravel.model.GetPostsModel
import com.example.gasytravel.model.LoginModel
import com.example.gasytravel.model.LoginResponseModel
import com.example.gasytravel.model.Post
import com.example.gasytravel.model.UploadBodyModel
import com.example.gasytravel.model.UploadResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("posts")
    fun getPosts(@Query("page") page: Int): Call<GetPostsModel>

    @POST("posts/upload")
    fun uploadFile(@Body post: UploadBodyModel): Call<UploadResponseModel>

    @POST("posts")
    fun createPost(@Body post: Post): Call<Post>

    @POST("api/auth")
    fun login(@Body login: LoginModel): Call<LoginResponseModel>

}