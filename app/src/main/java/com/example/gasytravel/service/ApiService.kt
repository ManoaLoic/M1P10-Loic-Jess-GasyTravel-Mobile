package com.example.gasytravel.service

import com.example.gasytravel.model.GetPostsModel
import com.example.gasytravel.model.Post
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("posts")
    fun getPosts(@Query("page") page: Int): Call<GetPostsModel>

    @GET("posts/{id}")
    fun getPostDetails(@Path("id") postId: String?): Call<Post>

}

