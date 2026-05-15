package com.example.shishusneh.ui.vaccination

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shishusneh.data.model.Vaccination
import com.example.shishusneh.databinding.ItemVaccinationBinding
import com.example.shishusneh.utils.DateUtils

class VaccinationAdapter(
    private val onCheckChanged: (Vaccination) -> Unit
) : ListAdapter<Vaccination, VaccinationAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(val binding: ItemVaccinationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVaccinationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vaccination = getItem(position)
        with(holder.binding) {
            tvVaccineName.text = vaccination.vaccineName
            tvDescription.text = vaccination.description
            tvDueDate.text = "Due: ${DateUtils.formatForDisplay(vaccination.scheduledDate)}"
            tvDisease.text = vaccination.diseasePrevented
            cbDone.isChecked = vaccination.isDone

            cbDone.setOnClickListener {
                onCheckChanged(vaccination)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Vaccination>() {
        override fun areItemsTheSame(oldItem: Vaccination, newItem: Vaccination) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Vaccination, newItem: Vaccination) =
            oldItem == newItem
    }
}