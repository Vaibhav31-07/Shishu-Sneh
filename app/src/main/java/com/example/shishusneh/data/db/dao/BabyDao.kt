package com.example.shishusneh.data.db.dao

import androidx.room.*
import com.example.shishusneh.data.model.Baby
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaby(baby: Baby)

    @Query("SELECT * FROM baby LIMIT 1")
    fun getBaby(): Flow<Baby?>

    @Update
    suspend fun updateBaby(baby: Baby)

    @Delete
    suspend fun deleteBaby(baby: Baby)
}