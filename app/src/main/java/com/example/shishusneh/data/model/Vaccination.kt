package com.example.shishusneh.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vaccination")
data class Vaccination(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val babyId: Int,
    val vaccineName: String,
    val scheduledDate: String,  // format: "dd-MM-yyyy"
    val isDone: Boolean = false,
    val diseasePrevented: String,
    val description: String = ""
)