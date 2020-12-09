package com.example.hw8

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reset.setOnClickListener {
            lifecycleScope.launch { downloadPosts() }
        }

        fab.setOnClickListener {
            lifecycleScope.launch { postPost() }
        }

        findButton.setOnClickListener {
            lifecycleScope.launch {
                val filtered =
                    searchPosts(editText.text.toString(), PostApp.instance.db.postDao().getAllPosts())
                PostApp.instance.posts.clear()
                PostApp.instance.posts.addAll(filtered)
                myRecyclerView.adapter?.notifyDataSetChanged()
            }
        }

        savedInstanceState ?: run {
            lifecycleScope.launch {
                PostApp.instance.posts.clear()
                PostApp.instance.posts.addAll(PostApp.instance.db.postDao().getAllPosts())
                viewPosts()
            }
        }
        viewPosts()
    }

    private suspend fun postPost() {
        val newId =
            (PostApp.instance.db.postDao().getAllPosts().maxByOrNull { p -> p.id }?.id ?: 100) + 1
        val post = Post(
            newId,
            1,
            "New title!",
            "New body!"
        )
        PostApp.instance.db.postDao().insert(post)
        PostApp.instance.posts.add(post)

        myRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showToast(msg: String) {
        Toast.makeText(
            this@MainActivity,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun searchPosts(filter: String, postList: List<Post>): List<Post> {
        return postList.filter { p -> p.title.contains(filter) }
    }

    private fun viewPosts() {
        progressBar.visibility = View.INVISIBLE
        val viewManager = LinearLayoutManager(this)
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = PostAdapter(PostApp.instance.posts) {
                lifecycleScope.launch { deletePost(it) }
            }
        }
    }

    private suspend fun deletePost(post: Post) {
        PostApp.instance.db.postDao().delete(post)
        PostApp.instance.posts.remove(post)
        myRecyclerView.adapter?.notifyDataSetChanged()
    }

    private suspend fun downloadPosts() {
        try {
            progressBar.visibility = View.VISIBLE
            val newPosts = PostApp.instance.service.listPosts()

            val oldPosts = PostApp.instance.db.postDao().getAllPosts()
            for (post in oldPosts) {
                PostApp.instance.db.postDao().delete(post)
            }

            for (post in newPosts) {
                PostApp.instance.db.postDao().insert(post)
            }

            PostApp.instance.posts.clear()
            PostApp.instance.posts.addAll(newPosts)

            progressBar.visibility = View.INVISIBLE
            myRecyclerView.adapter?.notifyDataSetChanged()
            showToast("List downloaded")
        } catch (_: Exception) {
            progressBar.visibility = View.INVISIBLE
            showToast("Connection Error")
        }
    }
}
