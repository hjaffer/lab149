package com.example.lab149application.framework.presentation.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lab149application.business.domain.models.SnapDao
import com.example.lab149application.databinding.ListItemSnapBinding

class SnapAdapter(
    private val onClickListener: (SnapDao, Int) -> Unit
) : ListAdapter<SnapDao, RecyclerView.ViewHolder>(SnapDiffCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val snap = getItem(position)
        (holder as SnapViewHolder).bind(snap)
        holder.itemView.setOnClickListener {
            onClickListener.invoke(snap, position)
        }
    }

    fun setImage(position: Int, bmp: Bitmap) {
        val snapDao = getItem(position)
        snapDao.bmp = bmp
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SnapViewHolder(
            ListItemSnapBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    class SnapViewHolder(
        private val binding: ListItemSnapBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SnapDao) {
            binding.apply {
                snap = item
                item.bmp?.let{ snapItemImage.setImageBitmap(item.bmp) } ?: run { snapItemImage.setImageResource(android.R.color.transparent); }
                executePendingBindings()
            }
        }
    }
}

private class SnapDiffCallback : DiffUtil.ItemCallback<SnapDao>() {

    override fun areItemsTheSame(oldItem: SnapDao, newItem: SnapDao): Boolean {
        return oldItem.id == newItem.id && oldItem.name == newItem.name && oldItem.bmp == newItem.bmp
    }

    override fun areContentsTheSame(oldItem: SnapDao, newItem: SnapDao): Boolean {
        return oldItem == newItem
    }
}
