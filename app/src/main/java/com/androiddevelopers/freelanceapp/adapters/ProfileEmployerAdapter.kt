package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowDiscoverPostsProfileBinding
import com.androiddevelopers.freelanceapp.databinding.RowEmployerPostsProfileBinding
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.bumptech.glide.Glide

class ProfileEmployerAdapter : RecyclerView.Adapter<ProfileEmployerAdapter.ProfileEmployerViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<EmployerJobPost>() {
        override fun areItemsTheSame(oldItem: EmployerJobPost, newItem: EmployerJobPost): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: EmployerJobPost, newItem: EmployerJobPost): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var postList: List<EmployerJobPost>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class ProfileEmployerViewHolder(val binding: RowEmployerPostsProfileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileEmployerViewHolder {
        val binding = RowEmployerPostsProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileEmployerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ProfileEmployerViewHolder, position: Int) {
        val post = postList[position]
        Glide.with(holder.itemView.context).load(post.images?.get(0)).into(holder.binding.ivEmployerPost)
        holder.binding.apply {
            employerPost = post
        }
        holder.itemView.setOnClickListener {

        }
    }
}


