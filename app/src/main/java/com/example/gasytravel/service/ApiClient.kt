package com.example.gasytravel.service

import com.example.gasytravel.model.GetPostsModel
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private val BASE_URL = "http://192.168.88.18:5000"
    private var apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun callGetPosts(page: Int, callback: Callback<GetPostsModel>) {
        apiService.getPosts(page).enqueue(callback)
    }
}