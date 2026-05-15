package com.example.shishusneh.ui.report

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shishusneh.ai.GeminiHelper
import com.example.shishusneh.data.db.AppDatabase
import com.example.shishusneh.databinding.ActivityBabyReportBinding
import com.example.shishusneh.utils.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class BabyReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBabyReportBinding
    private lateinit var db: AppDatabase
    private var babyId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBabyReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        babyId = intent.getIntExtra("BABY_ID", -1)
        binding.btnBack.setOnClickListener { finish() }

        if (babyId != -1) {
            generateReport()
        } else {
            Toast.makeText(this, "Error: Baby profile not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnShareReport.setOnClickListener {
            shareReport()
        }
    }

    private fun generateReport() {
        lifecycleScope.launch {
            val baby = db.babyDao().getBabyById(babyId).first() ?: return@launch
            val growthEntries = db.weightDao().getWeightEntries(baby.id).first()
            val vaccinations = db.vaccinationDao().getVaccinations(baby.id).first()

            // Update UI with basic info
            binding.tvReportBabyName.text = baby.name
            val days = DateUtils.daysBetween(baby.dateOfBirth, DateUtils.today())
            val months = days / 30
            binding.tvReportBabyDetails.text = String.format(Locale.getDefault(), "Age: %d months | Gender: %s", months.toInt(), baby.gender)

            // Growth summary
            if (growthEntries.isNotEmpty()) {
                val latest = growthEntries.last()
                val initial = growthEntries.first()
                val totalGain = latest.weightKg - initial.weightKg
                binding.tvGrowthStats.text = String.format(Locale.getDefault(), 
                    "Current Weight: %.1fkg | Height: %.1fcm\nTotal Weight Gain: +%.1fkg", 
                    latest.weightKg, latest.heightCm, totalGain)
            } else {
                binding.tvGrowthStats.text = "No growth data recorded yet."
            }

            // Vaccine status
            val taken = vaccinations.count { it.isDone }
            val pending = vaccinations.count { !it.isDone }
            binding.tvVaccineStatus.text = String.format(Locale.getDefault(), "%d Taken | %d Pending", taken, pending)

            // AI Insight
            binding.tvAiReport.text = "Analyzing data with AI... 🧠"
            
            val prompt = """
                Generate a professional and encouraging baby wellness report for:
                Name: ${baby.name}
                Age: $months months
                Growth History: ${growthEntries.joinToString { "${it.weightKg}kg on ${it.date}" }}
                Vaccinations: $taken completed, $pending remaining.
                
                Provide a summary of progress, nutrition advice for this age, and one encouraging tip for parents. Keep it concise.
            """.trimIndent()

            try {
                val insight = GeminiHelper.ask(prompt)
                binding.tvAiReport.text = insight
            } catch (e: Exception) {
                binding.tvAiReport.text = "AI report generation failed. Please check your internet connection."
            }
        }
    }

    private fun shareReport() {
        val reportContent = """
            BABY WELLNESS REPORT - SHISHU SNEH
            ----------------------------------
            Baby: ${binding.tvReportBabyName.text}
            Details: ${binding.tvReportBabyDetails.text}
            
            GROWTH:
            ${binding.tvGrowthStats.text}
            
            VACCINATIONS:
            ${binding.tvVaccineStatus.text}
            
            AI INSIGHTS:
            ${binding.tvAiReport.text}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Baby Wellness Report")
            putExtra(Intent.EXTRA_TEXT, reportContent)
        }
        startActivity(Intent.createChooser(intent, "Share Report via"))
    }
}
