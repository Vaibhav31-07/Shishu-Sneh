package com.example.shishusneh.data.db.dao

import androidx.room.*
import com.example.shishusneh.data.model.Baby
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaby(baby: Baby): Long

    @Query("SELECT * FROM baby")
    fun getAllBabies(): Flow<List<Baby>>

    @Query("SELECT * FROM baby LIMIT 1")
    fun getBaby(): Flow<Baby?>

    @Query("SELECT * FROM baby WHERE id = :id")
    fun getBabyById(id: Int): Flow<Baby?>

    @Query("SELECT * FROM baby WHERE id = :id")
    suspend fun getBabyByIdOnce(id: Int): Baby?

    @Query("SELECT * FROM baby LIMIT 1")
    suspend fun getBabyOnce(): Baby?

    @Update
    suspend fun updateBaby(baby: Baby)

    @Delete
    suspend fun deleteBaby(baby: Baby)
}