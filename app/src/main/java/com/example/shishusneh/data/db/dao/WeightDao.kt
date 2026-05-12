package com.example.shishusneh.data.db.dao

import androidx.room.*
import com.example.shishusneh.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(entry: WeightEntry)

    @Query("SELECT * FROM weight_entry WHERE babyId = :babyId ORDER BY date ASC")
    fun getWeightEntries(babyId: Int): Flow<List<WeightEntry>>

    @Delete
    suspend fun deleteWeight(entry: WeightEntry)
}