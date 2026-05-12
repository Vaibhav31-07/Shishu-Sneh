package com.example.shishusneh.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "baby")
data class Baby(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val dateOfBirth: String,
    val gender: String
)