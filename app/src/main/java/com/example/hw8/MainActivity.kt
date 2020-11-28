package com.example.hw8

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        reset.setOnClickListener {
            downloadPosts()
        }

        fab.setOnClickListener {
            postPost()
        }

        findButton.setOnClickListener {
            SearchPostsAsyncTask(this).execute(editText.text.toString())
        }

        savedInstanceState ?: run {
            AllPostsAsyncTask(this).execute()
        }
        viewPosts()
    }

    private fun postPost() {
        PostPostAsyncTask(this).execute()
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

    fun viewPosts() {
        progressBar.visibility = View.INVISIBLE
        val viewManager = LinearLayoutManager(this)
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = PostAdapter(PostApp.instance.posts) {
                DeletePostAsyncTask(this@MainActivity).execute(it)
            }
        }
    }

    class AllPostsAsyncTask(activity: MainActivity) : AsyncTask<Unit, Unit, Unit>() {
        private val weakActivity = WeakReference(activity)
        override fun doInBackground(vararg p0: Unit?) {
            PostApp.instance.posts.clear()
            PostApp.instance.posts.addAll(PostApp.instance.db.postDao().allPosts)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            weakActivity.get()?.viewPosts()
        }
    }

    class SearchPostsAsyncTask(activity: MainActivity) : AsyncTask<String, Unit, List<Post>>() {
        private val weakActivity = WeakReference(activity)
        override fun doInBackground(vararg params: String?): List<Post> {
            val filter = params[0] ?: ""
            return weakActivity.get()?.searchPosts(filter, PostApp.instance.db.postDao().allPosts) ?: ArrayList()
        }

        override fun onPostExecute(result: List<Post>?) {
            super.onPostExecute(result)
            when {
                result != null -> {
                    PostApp.instance.posts.clear()
                    PostApp.instance.posts.addAll(result)
                }
            }
            weakActivity.get()?.myRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }

    class DeletePostAsyncTask(activity: MainActivity) : AsyncTask<Post, Unit, Unit>() {
        private val weakActivity = WeakReference(activity)
        override fun doInBackground(vararg posts: Post?): Unit {
            PostApp.instance.db.postDao().delete(posts[0])
            PostApp.instance.posts.remove(posts[0])
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            weakActivity.get()?.myRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }

    class PostPostAsyncTask(activity: MainActivity) : AsyncTask<Unit, Unit, Unit>() {
        private val weakActivity = WeakReference(activity)
        override fun doInBackground(vararg p0: Unit?): Unit {
            val newId = (PostApp.instance.db.postDao().allPosts.maxByOrNull { p -> p.id }?.id ?: 100) + 1
            val post = Post(
                    newId,
                    1,
                    "New title!",
                    "New body!"
            )
            PostApp.instance.db.postDao().insert(post)
            PostApp.instance.posts.add(post)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            weakActivity.get()?.myRecyclerView?.adapter?.notifyDataSetChanged()
        }
    }

    class ResetAsyncTask(activity: MainActivity) : AsyncTask<List<Post>, Unit, Unit>() {
        private val weakActivity = WeakReference(activity)
        override fun doInBackground(vararg postsDownloaded: List<Post>?) {
            val posts = PostApp.instance.db.postDao().allPosts
            for (post in posts) {
                PostApp.instance.db.postDao().delete(post)
            }
            for (post in postsDownloaded[0]!!) {
                PostApp.instance.db.postDao().insert(post)
            }
            PostApp.instance.posts.clear()
            PostApp.instance.posts.addAll(postsDownloaded[0]!!)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            weakActivity.get()?.progressBar?.visibility = View.INVISIBLE
            weakActivity.get()?.myRecyclerView?.adapter?.notifyDataSetChanged()
            weakActivity.get()?.showToast("List downloaded")
        }
    }

    private fun downloadPosts() {
        progressBar.visibility = View.VISIBLE
        val callPosts = PostApp.instance.service.listPosts()
        callPosts.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                Log.d("Post Api", "DOWNLOAD SUCCESSFUL")
                ResetAsyncTask(this@MainActivity).execute(response.body()!!)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.e("Post Api", "failed", t)
                progressBar.visibility = View.INVISIBLE
                showToast("Connection error")
            }
        })
    }
}