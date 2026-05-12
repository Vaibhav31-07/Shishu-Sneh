package com.example.shishusneh.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val FORMAT = "dd-MM-yyyy"

    fun today(): String {
        return SimpleDateFormat(FORMAT, Locale.getDefault()).format(Date())
    }

    private fun parseDate(dateStr: String): Date? {
        val formats = listOf("dd-MM-yyyy", "dd/MM/yyyy")
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.isLenient = false
                return sdf.parse(dateStr)
            } catch (e: Exception) {
                // Try next format
            }
        }
        return null
    }

    fun addDaysToDate(dateStr: String, days: Int): String {
        val date = parseDate(dateStr) ?: return dateStr
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return SimpleDateFormat(FORMAT, Locale.getDefault()).format(calendar.time)
    }

    fun daysBetween(startDate: String, endDate: String): Long {
        val start = parseDate(startDate) ?: return 0
        val end = parseDate(endDate) ?: return 0
        val diff = end.time - start.time
        return diff / (1000 * 60 * 60 * 24)
    }

    fun formatForDisplay(dateStr: String): String {
        val date = parseDate(dateStr) ?: return dateStr
        return try {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            dateStr
        }
    }
}