package com.example.gasytravel.service
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.gasytravel.model.GetPostsModel
import com.example.gasytravel.model.Post

class ApiClient {
    private val BASE_URL = "http://192.168.88.19:5000/"
    private val authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NGM4ZmM1NWEyMmIzMjc2NDZlMjYwOWMiLCJuYW1lIjoiTG9pYyIsImVtYWlsIjoibG9yYW5kcmlhbmFyaXZlbG9AZ21haWwuY29tIiwidXNlclR5cGUiOiI2NGM4ZmMxNzg3ZTEwNDdhODcwZDJmMzciLCJpYXQiOjE2OTA5ODg5MjB9.CCRpZCgQZwBuxRQQ84LHLLYl2xhSL4ubQzH87OfvUhg"

    private val authInterceptor = Interceptor { chain ->
        val originalRequest: Request = chain.request()
        val newRequest: Request = originalRequest.newBuilder()
            .header("x-auth-token", authToken)
            .build()
        chain.proceed(newRequest)
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    private var apiService: ApiService = retrofit.create(ApiService::class.java)

    fun callGetPosts(page: Int, callback: Callback<GetPostsModel>) {
        apiService.getPosts(page).enqueue(callback)
    }
    fun callGetPostDetails(postId: String?, callback: Callback<Post>) {
        apiService.getPostDetails(postId).enqueue(callback)
    }
}
