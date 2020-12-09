package com.example.hw8

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostDao {
    @Insert
    suspend fun insert(post: Post)

    @Delete
    suspend fun delete(post: Post)

    @Query("SELECT * FROM post")
    suspend fun getAllPosts(): List<Post>
}