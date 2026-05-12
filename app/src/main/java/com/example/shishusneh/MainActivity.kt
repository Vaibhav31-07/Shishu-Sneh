package com.example.shishusneh

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shishusneh.ui.onboarding.OnboardingActivity
import com.example.shishusneh.worker.VaccinationWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Schedule daily vaccination check
        val workRequest = PeriodicWorkRequestBuilder<VaccinationWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueue(workRequest)

        // Go to Onboarding
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }
}