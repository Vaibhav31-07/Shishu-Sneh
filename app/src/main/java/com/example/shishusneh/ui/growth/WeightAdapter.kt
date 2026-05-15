package com.example.shishusneh.ui.growth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shishusneh.data.model.WeightEntry
import com.example.shishusneh.databinding.ItemWeightBinding

class WeightAdapter(private val onDeleteClick: (WeightEntry) -> Unit) :
    ListAdapter<WeightEntry, WeightAdapter.WeightViewHolder>(WeightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeightViewHolder {
        val binding = ItemWeightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeightViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WeightViewHolder(private val binding: ItemWeightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: WeightEntry) {
            binding.tvDate.text = entry.date
            binding.tvWeightHeight.text = "Weight: ${entry.weightKg} kg | Height: ${entry.heightCm} cm"
            binding.btnDelete.setOnClickListener {
                onDeleteClick(entry)
            }
        }
    }

    class WeightDiffCallback : DiffUtil.ItemCallback<WeightEntry>() {
        override fun areItemsTheSame(oldItem: WeightEntry, newItem: WeightEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WeightEntry, newItem: WeightEntry): Boolean {
            return oldItem == newItem
        }
    }
}