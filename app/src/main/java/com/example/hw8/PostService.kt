package com.example.hw8

import retrofit2.http.GET

interface PostService {
    @GET("posts")
    suspend fun listPosts(): List<Post>
}