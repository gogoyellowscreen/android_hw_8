package com.example.hw8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostAdapter(
    val posts: List<Post>,
    private val deleteFunc: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    class PostViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
        fun bind(post: Post) {
            with(root) {
                title.text = post.title
                body.text = post.body
                postId.text = post.id.toString()
                userId.text = post.userId.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val holder = PostViewHolder(
            LayoutInflater.
            from(parent.context).
            inflate(R.layout.list_item, parent, false)
        )

        holder.root.button.setOnClickListener {
            deleteFunc(posts[holder.adapterPosition])
        }

        return holder
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }
}