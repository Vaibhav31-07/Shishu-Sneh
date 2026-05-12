package com.example.shishusneh.data.db.dao

import androidx.room.*
import com.example.shishusneh.data.model.Vaccination
import kotlinx.coroutines.flow.Flow

@Dao
interface VaccinationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccination(vaccination: Vaccination)

    @Query("SELECT * FROM vaccination WHERE babyId = :babyId ORDER BY scheduledDate ASC")
    fun getVaccinations(babyId: Int): Flow<List<Vaccination>>

    @Query("SELECT * FROM vaccination")
    fun getAllVaccinations(): Flow<List<Vaccination>>

    @Update
    suspend fun updateVaccination(vaccination: Vaccination)

    @Query("SELECT * FROM vaccination WHERE scheduledDate = :date AND isDone = 0")
    suspend fun getVaccinationsDueOn(date: String): List<Vaccination>
}