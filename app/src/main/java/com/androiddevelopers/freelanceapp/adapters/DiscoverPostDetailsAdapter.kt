package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowDiscoverBinding
import com.androiddevelopers.freelanceapp.databinding.RowDiscoverDetailsBinding
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.view.DiscoverFragmentDirections
import com.bumptech.glide.Glide

class DiscoverPostDetailsAdapter : RecyclerView.Adapter<DiscoverPostDetailsAdapter.DiscoverPostDetailsViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<DiscoverPostModel>() {
        override fun areItemsTheSame(oldItem: DiscoverPostModel, newItem: DiscoverPostModel): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DiscoverPostModel, newItem: DiscoverPostModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var postList: List<DiscoverPostModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class DiscoverPostDetailsViewHolder(val binding: RowDiscoverDetailsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverPostDetailsViewHolder {
        val binding = RowDiscoverDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiscoverPostDetailsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: DiscoverPostDetailsViewHolder, position: Int) {
        val post = postList[position]
        Glide.with(holder.itemView.context).load(post.images?.get(0)).into(holder.binding.ivPost)
        holder.binding.apply {
            postItem = post
        }
    }
}
