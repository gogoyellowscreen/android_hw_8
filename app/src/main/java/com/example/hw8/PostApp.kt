package com.example.hw8

import android.app.Application
import androidx.room.Room
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostApp : Application() {
    lateinit var retrofit: Retrofit
    lateinit var service: PostService
    lateinit var db: PostDatabase
    var posts: ArrayList<Post> = ArrayList()
    //var posts = ArrayList<Post>()
    private val baseUrl = "https://jsonplaceholder.typicode.com/"

    override fun onCreate() {
        super.onCreate()
        instance = this

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(PostService::class.java)

        db = Room
            .databaseBuilder(applicationContext, PostDatabase::class.java, "post-database")
            .build()
    }

    companion object {
        lateinit var instance: PostApp
            private set
    }
}