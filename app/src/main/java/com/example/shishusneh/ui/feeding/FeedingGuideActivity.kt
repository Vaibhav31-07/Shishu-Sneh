package com.example.shishusneh.ui.feeding

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shishusneh.ai.GeminiHelper
import com.example.shishusneh.data.db.AppDatabase
import com.example.shishusneh.data.model.Baby
import com.example.shishusneh.databinding.ActivityFeedingGuideBinding
import com.example.shishusneh.utils.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FeedingGuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedingGuideBinding
    private lateinit var db: AppDatabase
    private var currentBaby: Baby? = null
    private var babyId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedingGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        babyId = intent.getIntExtra("BABY_ID", -1)
        binding.btnBack.setOnClickListener { finish() }

        loadBabyData()

        binding.btnAskAI.setOnClickListener {
            val question = binding.etAIQuestion.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, "Please type a question!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            binding.tvAIResponse.visibility = View.VISIBLE
            binding.tvAIResponse.text = "Thinking... 🤔"
            binding.btnAskAI.isEnabled = false
            
            lifecycleScope.launch {
                try {
                    val babyContext = currentBaby?.let { 
                        val months = DateUtils.daysBetween(it.dateOfBirth, DateUtils.today()) / 30
                        "I have a baby named ${it.name} who is $months months old (${it.gender})."
                    } ?: "I'm asking about general baby nutrition."
                    
                    val prompt = """
                        You are Shishu Sneh's expert baby healthcare and nutrition assistant.
                        Context: $babyContext
                        Question: $question
                        Please provide a helpful, safe, and concise response. 
                        If the question is not about babies or nutrition, politely redirect the parent.
                    """.trimIndent()
                    
                    val response = GeminiHelper.ask(prompt)
                    binding.tvAIResponse.text = response
                } catch (e: Exception) {
                    Log.e("FeedingGuide", "AI Error: ${e.message}")
                    binding.tvAIResponse.text = "Error: ${e.localizedMessage ?: "Connectivity issue."}"
                } finally {
                    binding.btnAskAI.isEnabled = true
                }
            }
        }
    }

    private fun loadBabyData() {
        lifecycleScope.launch {
            val baby = if (babyId != -1) {
                db.babyDao().getBabyById(babyId).first()
            } else {
                db.babyDao().getBaby().first()
            }
            
            currentBaby = baby
            if (baby != null) {
                val days = DateUtils.daysBetween(baby.dateOfBirth, DateUtils.today())
                val months = days / 30
                binding.tvFeedingTip.text = getFeedingTip(months)
            }
        }
    }

    private fun getFeedingTip(months: Long): String {
        return when {
            months < 0 -> "Check baby's Date of Birth settings! 👶"
            months < 6 -> "🤱 Exclusive breastfeeding is recommended for the first 6 months."
            months < 9 -> "🥣 Start soft foods like mashed banana, rice cereal."
            months < 12 -> "🍎 Introduce mashed fruits, vegetables, and dal."
            else -> "🍽️ Baby can now eat most family foods."
        }
    }
}
