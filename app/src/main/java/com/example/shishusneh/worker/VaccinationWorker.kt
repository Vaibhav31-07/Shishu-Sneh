package com.example.shishusneh.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shishusneh.data.db.AppDatabase
import com.example.shishusneh.utils.DateUtils

class VaccinationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val today = DateUtils.today()
        val dueVaccinations = db.vaccinationDao().getVaccinationsDueOn(today)

        if (dueVaccinations.isNotEmpty()) {
            dueVaccinations.forEach { vaccination ->
                showNotification(
                    title = "💉 Vaccination Due Today!",
                    message = "${vaccination.vaccineName} — Prevents ${vaccination.diseasePrevented}"
                )
            }
        }
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "vaccination_channel"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Vaccination Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}