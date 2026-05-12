package com.example.shishusneh.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shishusneh.data.db.AppDatabase
import com.example.shishusneh.databinding.ActivityHomeBinding
import com.example.shishusneh.ui.feeding.FeedingGuideActivity
import com.example.shishusneh.ui.growth.GrowthChartActivity
import com.example.shishusneh.ui.milestone.MilestoneActivity
import com.example.shishusneh.ui.onboarding.OnboardingActivity
import com.example.shishusneh.ui.vaccination.VaccinationActivity
import com.example.shishusneh.utils.DateUtils
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            db.babyDao().getBaby().collect { baby ->
                if (baby != null) {
                    binding.tvWelcome.text = "Hello, ${baby.name}! 👶"
                    val days = DateUtils.daysBetween(baby.dateOfBirth, DateUtils.today())
                    val months = days / 30
                    binding.tvAge.text = "Age: $months months old"
                }
            }
        }

        binding.cardGrowth.setOnClickListener {
            startActivity(Intent(this, GrowthChartActivity::class.java))
        }

        binding.cardVaccination.setOnClickListener {
            startActivity(Intent(this, VaccinationActivity::class.java))
        }

        binding.cardFeeding.setOnClickListener {
            startActivity(Intent(this, FeedingGuideActivity::class.java))
        }

        binding.cardMilestone.setOnClickListener {
            startActivity(Intent(this, MilestoneActivity::class.java))
        }

        // Add a way to go back to onboarding (Profile/Edit)
        binding.tvWelcome.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity::class.java))
        }
    }
}