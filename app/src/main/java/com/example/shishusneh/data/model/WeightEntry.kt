package com.example.shishusneh.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entry")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val babyId: Int,
    val weightKg: Float,
    val heightCm: Float,
    val date: String            // format: "dd-MM-yyyy"
)