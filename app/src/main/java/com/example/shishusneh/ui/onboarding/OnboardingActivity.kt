package com.example.shishusneh.ui.onboarding

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
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
    private var isAddingNew: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        isAddingNew = intent.getBooleanExtra("ADD_NEW_BABY", false)

        // Entrance animations for a unique feel
        animateEntrance()
        // Continuous "alive" animations
        startUniqueAnimations()

        // Handle UI state based on whether we are adding or editing
        if (isAddingNew) {
            binding.btnStart.text = getString(com.example.shishusneh.R.string.start_journey)
            binding.btnBack.visibility = View.VISIBLE
        } else {
            // Check if data exists to pre-fill
            lifecycleScope.launch {
                val baby = db.babyDao().getBaby().first()
                if (baby != null) {
                    existingBabyId = baby.id
                    binding.etBabyName.setText(baby.name)
                    binding.etDOB.setText(baby.dateOfBirth)
                    if (baby.gender == "Boy") binding.rbBoy.isChecked = true else binding.rbGirl.isChecked = true
                    binding.btnStart.text = getString(com.example.shishusneh.R.string.update_profile)
                    binding.btnBack.visibility = View.VISIBLE
                }
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
                if (isAddingNew || existingBabyId == 0) {
                    val baby = Baby(name = name, dateOfBirth = dob, gender = gender)
                    val newId = db.babyDao().insertBaby(baby)
                    setupVaccinations(newId.toInt(), dob)
                } else {
                    val baby = Baby(id = existingBabyId, name = name, dateOfBirth = dob, gender = gender)
                    db.babyDao().updateBaby(baby)
                }
                
                goToHome()
            }
        }
    }

    private fun animateEntrance() {
        binding.logoContainer.alpha = 0f
        binding.logoContainer.translationY = -100f
        binding.tvTitle.alpha = 0f
        binding.tvSubtitle.alpha = 0f
        binding.formCard.alpha = 0f
        binding.formCard.translationY = 200f
        binding.btnStart.alpha = 0f

        binding.logoContainer.animate().alpha(1f).translationY(0f).setDuration(1000).setInterpolator(android.view.animation.OvershootInterpolator()).start()
        binding.tvTitle.animate().alpha(1f).setStartDelay(400).setDuration(800).start()
        binding.tvSubtitle.animate().alpha(1f).setStartDelay(600).setDuration(800).start()
        binding.formCard.animate().alpha(1f).translationY(0f).setStartDelay(800).setDuration(1000).start()
        binding.btnStart.animate().alpha(1f).setStartDelay(1200).setDuration(800).start()
    }

    private fun startUniqueAnimations() {
        // Pulse animation for the background circle
        val pulse = ObjectAnimator.ofPropertyValuesHolder(
            binding.logoBg,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.15f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.15f)
        ).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

        // Floating animation for the entire logo container
        val float = ObjectAnimator.ofFloat(binding.logoContainer, View.TRANSLATION_Y, -15f, 15f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

        pulse.start()
        float.start()
    }

    private suspend fun setupVaccinations(babyId: Int, dob: String) {
        val vaccinations = listOf(
            Vaccination(
                babyId = babyId, 
                vaccineName = "BCG", 
                scheduledDate = dob, 
                diseasePrevented = "Tuberculosis",
                description = "Protects against Tuberculosis (TB) and is given at birth."
            ),
            Vaccination(
                babyId = babyId, 
                vaccineName = "OPV 0", 
                scheduledDate = dob, 
                diseasePrevented = "Polio",
                description = "Oral Polio Vaccine (at birth) to build immunity against Polio."
            ),
            Vaccination(
                babyId = babyId, 
                vaccineName = "Hepatitis B", 
                scheduledDate = dob, 
                diseasePrevented = "Hepatitis B",
                description = "Prevents Hepatitis B infection, which can cause liver damage."
            ),
            Vaccination(
                babyId = babyId, 
                vaccineName = "DPT 1", 
                scheduledDate = DateUtils.addDaysToDate(dob, 42), 
                diseasePrevented = "Diphtheria, Pertussis, Tetanus",
                description = "Protects against Diphtheria, Pertussis (Whooping Cough), and Tetanus."
            ),
            Vaccination(
                babyId = babyId, 
                vaccineName = "OPV 1", 
                scheduledDate = DateUtils.addDaysToDate(dob, 42), 
                diseasePrevented = "Polio",
                description = "First dose of Oral Polio Vaccine to continue building Polio immunity."
            ),
            Vaccination(
                babyId = babyId, 
                vaccineName = "DPT 2", 
                scheduledDate = DateUtils.addDaysToDate(dob, 70), 
                diseasePrevented = "Diphtheria, Pertussis, Tetanus",
                description = "Second booster dose for Diphtheria, Pertussis, and Tetanus."
            ),
            Vaccination(
                babyId = babyId, 
                vaccineName = "OPV 2", 
                scheduledDate = DateUtils.addDaysToDate(dob, 70), 
                diseasePrevented = "Polio",
                description = "Second dose of Oral Polio Vaccine."
            ),
            Vaccination(
                babyId = babyId, 
                vaccineName = "DPT 3", 
                scheduledDate = DateUtils.addDaysToDate(dob, 98), 
                diseasePrevented = "Diphtheria, Pertussis, Tetanus",
                description = "Third booster dose for long-term protection against DPT."
            ),
            Vaccination(
                babyId = babyId, 
                vaccineName = "OPV 3", 
                scheduledDate = DateUtils.addDaysToDate(dob, 98), 
                diseasePrevented = "Polio",
                description = "Third dose of Oral Polio Vaccine for complete Polio protection."
            ),
            Vaccination(
                babyId = babyId, 
                vaccineName = "Measles", 
                scheduledDate = DateUtils.addDaysToDate(dob, 270), 
                diseasePrevented = "Measles",
                description = "Protects against Measles, a highly contagious viral infection."
            )
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