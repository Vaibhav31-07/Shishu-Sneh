package com.example.shishusneh.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shishusneh.data.db.AppDatabase
import com.example.shishusneh.data.model.Baby
import com.example.shishusneh.data.model.Vaccination
import com.example.shishusneh.databinding.ActivityOnboardingBinding
import com.example.shishusneh.ui.home.HomeActivity
import com.example.shishusneh.utils.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var db: AppDatabase
    private var existingBabyId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Check if data exists to pre-fill
        lifecycleScope.launch {
            val baby = db.babyDao().getBaby().first()
            if (baby != null) {
                existingBabyId = baby.id
                binding.etBabyName.setText(baby.name)
                binding.etDOB.setText(baby.dateOfBirth)
                if (baby.gender == "Boy") binding.rbBoy.isChecked = true else binding.rbGirl.isChecked = true
                binding.btnStart.text = "Update Profile"
                
                // Show back button if profile exists
                binding.btnBack.visibility = View.VISIBLE
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnStart.setOnClickListener {
            val name = binding.etBabyName.text.toString().trim()
            val dob = binding.etDOB.text.toString().trim()
            val gender = if (binding.rbBoy.isChecked) "Boy" else "Girl"

            if (name.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Please fill all details!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val baby = Baby(id = existingBabyId, name = name, dateOfBirth = dob, gender = gender)
                if (existingBabyId == 0) {
                    db.babyDao().insertBaby(baby)
                } else {
                    db.babyDao().updateBaby(baby)
                }
                
                // Only setup vaccinations if they don't exist yet
                val existingVaccines = db.vaccinationDao().getAllVaccinations().first()
                if (existingVaccines.isEmpty()) {
                    setupVaccinations(1, dob)
                }
                
                goToHome()
            }
        }
    }

    private suspend fun setupVaccinations(babyId: Int, dob: String) {
        val vaccinations = listOf(
            Vaccination(babyId = babyId, vaccineName = "BCG", scheduledDate = dob, diseasePrevented = "Tuberculosis"),
            Vaccination(babyId = babyId, vaccineName = "OPV 0", scheduledDate = dob, diseasePrevented = "Polio"),
            Vaccination(babyId = babyId, vaccineName = "Hepatitis B", scheduledDate = dob, diseasePrevented = "Hepatitis B"),
            Vaccination(babyId = babyId, vaccineName = "DPT 1", scheduledDate = DateUtils.addDaysToDate(dob, 42), diseasePrevented = "Diphtheria, Pertussis, Tetanus"),
            Vaccination(babyId = babyId, vaccineName = "OPV 1", scheduledDate = DateUtils.addDaysToDate(dob, 42), diseasePrevented = "Polio"),
            Vaccination(babyId = babyId, vaccineName = "DPT 2", scheduledDate = DateUtils.addDaysToDate(dob, 70), diseasePrevented = "Diphtheria, Pertussis, Tetanus"),
            Vaccination(babyId = babyId, vaccineName = "OPV 2", scheduledDate = DateUtils.addDaysToDate(dob, 70), diseasePrevented = "Polio"),
            Vaccination(babyId = babyId, vaccineName = "DPT 3", scheduledDate = DateUtils.addDaysToDate(dob, 98), diseasePrevented = "Diphtheria, Pertussis, Tetanus"),
            Vaccination(babyId = babyId, vaccineName = "OPV 3", scheduledDate = DateUtils.addDaysToDate(dob, 98), diseasePrevented = "Polio"),
            Vaccination(babyId = babyId, vaccineName = "Measles", scheduledDate = DateUtils.addDaysToDate(dob, 270), diseasePrevented = "Measles")
        )
        vaccinations.forEach { db.vaccinationDao().insertVaccination(it) }
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}