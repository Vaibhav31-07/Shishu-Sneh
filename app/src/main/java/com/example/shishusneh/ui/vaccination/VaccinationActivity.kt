package com.example.shishusneh.ui.vaccination

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shishusneh.data.db.AppDatabase
import com.example.shishusneh.databinding.ActivityVaccinationBinding
import kotlinx.coroutines.launch

class VaccinationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVaccinationBinding
    private lateinit var db: AppDatabase
    private var babyId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVaccinationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        babyId = intent.getIntExtra("BABY_ID", -1)

        binding.btnBack.setOnClickListener { finish() }

        val adapter = VaccinationAdapter { vaccination ->
            lifecycleScope.launch {
                db.vaccinationDao().updateVaccination(vaccination.copy(isDone = !vaccination.isDone))
            }
        }

        binding.rvVaccinations.layoutManager = LinearLayoutManager(this)
        binding.rvVaccinations.adapter = adapter

        if (babyId != -1) {
            lifecycleScope.launch {
                db.vaccinationDao().getVaccinations(babyId).collect { list ->
                    adapter.submitList(list)
                }
            }
        }
    }
}