package com.example.gasytravel.service

import android.content.Context
import retrofit2.Call
import com.example.gasytravel.model.GetPostsModel
import com.example.gasytravel.model.LoginModel
import com.example.gasytravel.model.LoginResponseModel
import com.example.gasytravel.model.Post
import com.example.gasytravel.model.SignUpModel
import com.example.gasytravel.model.SignUpResponseModel
import com.example.gasytravel.model.UploadBodyModel
import com.example.gasytravel.model.UploadResponseModel

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

class ApiClient (context : Context) {
    private val BASE_URL = "http://192.168.88.19:5000"
    private val context: Context = context

    private val authInterceptor = Interceptor { chain ->
        val sharedPreferences = context.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        var token = sharedPreferences.getString("my_token", null)
        val originalRequest: Request = chain.request()
        val newRequest: Request = originalRequest.newBuilder()
            .header("x-auth-token", token ?: "")
            .build()
        chain.proceed(newRequest)
  }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    private var apiService: ApiService = retrofit.create(ApiService::class.java)

    fun callLogin(login: LoginModel, callback: Callback<LoginResponseModel>) {
        apiService.login(login).enqueue(callback)
    }

    fun callSignUp(signUpModel: SignUpModel, callback: Callback<SignUpResponseModel>) {
        val call: Call<SignUpResponseModel> = apiService.signUp(signUpModel)
        call.enqueue(callback)
    }

    fun callGetPosts(page: Int, callback: Callback<GetPostsModel>) {
        apiService.getPosts(page).enqueue(callback)
    }
    
    fun callGetPostDetails(postId: String?, callback: Callback<Post>) {
        apiService.getPostDetails(postId).enqueue(callback)
    }

    fun callUploadFile(body: UploadBodyModel, callback: Callback<UploadResponseModel>) {
        apiService.uploadFile(body).enqueue(callback)
    }

    fun callCreatePost(body: Post, callback: Callback<Post>) {
        apiService.createPost(body).enqueue(callback)
    }
}
