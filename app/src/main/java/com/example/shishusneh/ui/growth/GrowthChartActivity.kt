package com.example.shishusneh.ui.growth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.shishusneh.data.db.AppDatabase
import com.example.shishusneh.data.model.WeightEntry
import com.example.shishusneh.databinding.ActivityGrowthChartBinding
import com.example.shishusneh.utils.DateUtils
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

class GrowthChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGrowthChartBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrowthChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.btnBack.setOnClickListener { finish() }

        lifecycleScope.launch {
            db.weightDao().getWeightEntries(1).collect { entries ->
                updateChart(entries.map { it.weightKg })
            }
        }

        binding.btnAddWeight.setOnClickListener {
            val weight = binding.etWeight.text.toString().toFloatOrNull()
            val height = binding.etHeight.text.toString().toFloatOrNull()

            if (weight == null || height == null) {
                Toast.makeText(this, "Enter valid weight and height!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val entry = WeightEntry(
                    babyId = 1,
                    weightKg = weight,
                    heightCm = height,
                    date = DateUtils.today()
                )
                db.weightDao().insertWeight(entry)
                binding.etWeight.text?.clear()
                binding.etHeight.text?.clear()
                Toast.makeText(this@GrowthChartActivity, "Entry added! ✅", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateChart(weights: List<Float>) {
        val entries = weights.mapIndexed { index, weight ->
            Entry(index.toFloat(), weight)
        }
        val dataSet = LineDataSet(entries, "Weight (kg)").apply {
            color = 0xFFF06292.toInt()
            valueTextColor = 0xFF333333.toInt()
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(0xFFF06292.toInt())
        }
        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.invalidate()
    }
}