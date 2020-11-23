package com.example.hw8;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {
    @Insert
    void insert(Post post);

    @Delete
    void delete(Post post);

    @Query("SELECT * FROM post")
    List<Post> getAllPosts();
}
