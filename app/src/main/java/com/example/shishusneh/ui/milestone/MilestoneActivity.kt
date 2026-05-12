package com.example.shishusneh.ui.milestone

import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.shishusneh.databinding.ActivityMilestoneBinding

class MilestoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMilestoneBinding

    private val milestones = listOf(
        "1 Month" to listOf("Lifts head briefly", "Responds to sound", "Focuses on faces"),
        "2 Months" to listOf("Smiles socially", "Coos and makes sounds", "Follows objects with eyes"),
        "4 Months" to listOf("Holds head steady", "Laughs out loud", "Reaches for objects"),
        "6 Months" to listOf("Sits with support", "Babbles", "Recognizes familiar faces"),
        "9 Months" to listOf("Crawls", "Says mama/dada", "Pulls to stand"),
        "12 Months" to listOf("Walks with support", "Says 1-2 words", "Waves bye-bye")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMilestoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        milestones.forEach { (month, items) ->
            val header = TextView(this).apply {
                text = "📅 $month"
                textSize = 18f
                setTextColor(0xFFF06292.toInt())
                setPadding(0, 24, 0, 8)
            }
            binding.llMilestones.addView(header)

            items.forEach { milestone ->
                val checkBox = CheckBox(this).apply {
                    text = milestone
                    textSize = 15f
                    buttonTintList = android.content.res.ColorStateList.valueOf(0xFFF06292.toInt())
                }
                binding.llMilestones.addView(checkBox)
            }
        }
    }
}