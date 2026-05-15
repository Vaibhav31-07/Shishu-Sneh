package com.example.shishusneh.ui.growth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shishusneh.data.db.AppDatabase
import com.example.shishusneh.data.model.WeightEntry
import com.example.shishusneh.databinding.ActivityGrowthChartBinding
import com.example.shishusneh.utils.DateUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.launch

class GrowthChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGrowthChartBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: WeightAdapter
    private var babyId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGrowthChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        babyId = intent.getIntExtra("BABY_ID", -1)

        setupChart()
        setupRecyclerView()

        binding.btnBack.setOnClickListener { finish() }

        if (babyId != -1) {
            lifecycleScope.launch {
                db.weightDao().getWeightEntries(babyId).collect { entries ->
                    adapter.submitList(entries)
                    updateChart(entries.map { it.weightKg })
                    updateAnalysis(entries)
                }
            }
        }

        binding.btnAddWeight.setOnClickListener {
            val weightStr = binding.etWeight.text.toString().trim()
            val heightStr = binding.etHeight.text.toString().trim()

            val weight = weightStr.toFloatOrNull()
            val height = heightStr.toFloatOrNull()

            if (weight == null || height == null) {
                Toast.makeText(this, "Please enter valid numbers!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (babyId == -1) {
                Toast.makeText(this, "Error: No baby profile found!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val entry = WeightEntry(
                    babyId = babyId, // Fixed: use passed babyId instead of hardcoded 1
                    weightKg = weight,
                    heightCm = height,
                    date = DateUtils.today()
                )
                db.weightDao().insertWeight(entry)
                binding.etWeight.text?.clear()
                binding.etHeight.text?.clear()
                Toast.makeText(this@GrowthChartActivity, "Weight recorded! 📈", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = WeightAdapter { entry ->
            lifecycleScope.launch {
                db.weightDao().deleteWeight(entry)
                Toast.makeText(this@GrowthChartActivity, "Entry deleted", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvWeightEntries.apply {
            layoutManager = LinearLayoutManager(this@GrowthChartActivity)
            adapter = this@GrowthChartActivity.adapter
        }
    }

    private fun updateAnalysis(entries: List<WeightEntry>) {
        if (entries.isEmpty()) {
            binding.cardAnalysis.visibility = View.GONE
            return
        }

        binding.cardAnalysis.visibility = View.VISIBLE
        
        if (entries.size == 1) {
            binding.tvAnalysis.text = "First entry recorded! Keep logging to see your baby's growth trend."
            return
        }

        val lastEntry = entries.last()
        val previousEntry = entries[entries.size - 2]
        
        val weightDiff = lastEntry.weightKg - previousEntry.weightKg
        val heightDiff = lastEntry.heightCm - previousEntry.heightCm

        val analysisText = StringBuilder()
        
        when {
            weightDiff > 0 -> analysisText.append("Your baby has gained ${String.format("%.1f", weightDiff)}kg since the last check. Great progress! 🌟")
            weightDiff < 0 -> analysisText.append("There's a slight weight dip of ${String.format("%.1f", Math.abs(weightDiff))}kg. Monitor feeding patterns and consult a doctor if this continues.")
            else -> analysisText.append("Weight is steady at ${lastEntry.weightKg}kg. Consistency is a good sign of healthy maintenance.")
        }

        if (heightDiff > 0) {
            analysisText.append("\n\nAlso, your baby grew ${String.format("%.1f", heightDiff)}cm taller! Those little legs are getting longer. 📏")
        }

        binding.tvAnalysis.text = analysisText.toString()
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = 0xFF888888.toInt()
            }
            
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = 0xFFEEEEEE.toInt()
                textColor = 0xFF888888.toInt()
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = false
            
            animateX(1000)
        }
    }

    private fun updateChart(weights: List<Float>) {
        if (weights.isEmpty()) {
            binding.lineChart.clear()
            return
        }

        val entries = weights.mapIndexed { index, weight ->
            Entry(index.toFloat(), weight)
        }
        
        val dataSet = LineDataSet(entries, "Weight").apply {
            color = 0xFFF06292.toInt()
            setCircleColor(0xFFF06292.toInt())
            lineWidth = 3f
            circleRadius = 5f
            setDrawCircleHole(true)
            circleHoleRadius = 2.5f
            valueTextSize = 10f
            valueTextColor = 0xFFF06292.toInt()
            setDrawFilled(true)
            fillColor = 0xFFF06292.toInt()
            fillAlpha = 30
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.invalidate()
    }
}