package com.example.shishusneh.ui.milestone

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.shishusneh.databinding.ActivityMilestoneBinding
import com.example.shishusneh.databinding.ItemMilestoneCardBinding
import com.google.android.material.checkbox.MaterialCheckBox

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

        val pinkColor = 0xFFF06292.toInt()

        milestones.forEach { (month, items) ->
            val cardBinding = ItemMilestoneCardBinding.inflate(layoutInflater, binding.llMilestones, false)
            
            cardBinding.tvMonthHeader.text = "📅 $month"
            
            items.forEach { milestone ->
                val checkBox = MaterialCheckBox(this).apply {
                    text = milestone
                    textSize = 15f
                    buttonTintList = ColorStateList.valueOf(pinkColor)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
                cardBinding.llMilestoneItems.addView(checkBox)
            }
            
            binding.llMilestones.addView(cardBinding.root)
        }
    }
}