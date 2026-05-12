package com.example.shishusneh.ui.feeding

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shishusneh.ai.GeminiHelper
import com.example.shishusneh.data.db.AppDatabase
import com.example.shishusneh.databinding.ActivityFeedingGuideBinding
import com.example.shishusneh.utils.DateUtils
import kotlinx.coroutines.launch

class FeedingGuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedingGuideBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedingGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        binding.btnBack.setOnClickListener { finish() }

        lifecycleScope.launch {
            db.babyDao().getBaby().collect { baby ->
                if (baby != null) {
                    val days = DateUtils.daysBetween(baby.dateOfBirth, DateUtils.today())
                    val months = days / 30
                    val tip = getFeedingTip(months)
                    binding.tvFeedingTip.text = tip
                }
            }
        }

        binding.btnAskAI.setOnClickListener {
            val question = binding.etAIQuestion.text.toString().trim()
            if (question.isEmpty()) {
                Toast.makeText(this, "Please type a question!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.tvAIResponse.text = "Thinking... 🤔"
            lifecycleScope.launch {
                val response = GeminiHelper.ask("You are a baby healthcare expert. $question")
                binding.tvAIResponse.text = response
            }
        }
    }

    private fun getFeedingTip(months: Long): String {
        return when {
            months < 6 -> "🤱 Exclusive breastfeeding is recommended for the first 6 months. Feed every 2-3 hours."
            months < 9 -> "🥣 Start soft foods like mashed banana, rice cereal. Continue breastfeeding."
            months < 12 -> "🍎 Introduce mashed fruits, vegetables, and dal. Avoid salt and sugar."
            else -> "🍽️ Baby can now eat most family foods. Continue breastfeeding up to 2 years."
        }
    }
}