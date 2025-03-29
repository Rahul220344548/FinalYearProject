package com.example.gym_application.utils

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.gym_application.model.ClassWithScheduleModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object formatDateUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatExpirationDate(expirationDate : String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.US)
        val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)

        val date = LocalDate.parse(expirationDate, inputFormatter)
        return date.format(outputFormatter)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun newFormatExpirationDate(expirationDate : String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)
        val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)

        val date = LocalDate.parse(expirationDate, inputFormatter)
        return date.format(outputFormatter)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayDate(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)
        return today.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isClassOver(classModel: ClassWithScheduleModel): Boolean {
        return try {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val classDate = LocalDate.parse(classModel.classStartDate, dateFormatter)
            val classEndTime = LocalTime.parse(classModel.classEndTime, timeFormatter)
            val classDateTimeEnd = classDate.atTime(classEndTime)

            val now = LocalDateTime.now()
            now.isAfter(classDateTimeEnd)
        } catch (e: Exception) {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun adminFormattedDate(inDate : String) : String {
        return try {
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.US)
            val outputFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.US)

            val date = LocalDate.parse(inDate, inputFormatter)
            date.format(outputFormatter)

        } catch (e: Exception) {
            inDate
        }
    }

    fun convertTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return (hours * 60) + minutes
    }


}