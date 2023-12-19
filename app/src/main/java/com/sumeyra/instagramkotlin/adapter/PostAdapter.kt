package com.sumeyra.instagramkotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.squareup.picasso.Picasso
import com.sumeyra.instagramkotlin.databinding.RecyclerRowBinding
import com.sumeyra.instagramkotlin.model.Post

class PostAdapter (val postlist: ArrayList<Post>): RecyclerView.Adapter<PostAdapter.PostHolder>() {

    class PostHolder(var binding: RecyclerRowBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)

    }

    override fun getItemCount(): Int {
        return postlist.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerUserView.text= postlist.get(position).email
        holder.binding.recyclerViewComment.text =postlist.get(position).comment
        Picasso.get().load(postlist.get(position).downloadURL).into(holder.binding.recyclerImageView)

    }

}