package com.example.shishusneh.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shishusneh.data.db.dao.BabyDao
import com.example.shishusneh.data.db.dao.VaccinationDao
import com.example.shishusneh.data.db.dao.WeightDao
import com.example.shishusneh.data.model.Baby
import com.example.shishusneh.data.model.Vaccination
import com.example.shishusneh.data.model.WeightEntry

@Database(
    entities = [Baby::class, WeightEntry::class, Vaccination::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun babyDao(): BabyDao
    abstract fun weightDao(): WeightDao
    abstract fun vaccinationDao(): VaccinationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shishu_sneh_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}