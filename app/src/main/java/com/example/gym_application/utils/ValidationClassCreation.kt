package com.example.gym_application.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ValidationClassCreation {

    fun isValidClassTitle(title : String) : Boolean{
        return title.length >2
    }

    fun isValidTime(startTime: String, endTime: String): Boolean {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return try {
            val start = timeFormat.parse(startTime)
            val end = timeFormat.parse(endTime)

            if (start != null && end != null && start.before(end)) {

                val duration = (end.time - start.time) / (1000 * 60)
                return duration == 30L || duration == 60L
            }
            false
        } catch (e: Exception) {
            false
        }
    }
}