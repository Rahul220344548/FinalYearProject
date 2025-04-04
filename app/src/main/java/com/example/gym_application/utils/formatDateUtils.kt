package com.example.gym_application.utils

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.gym_application.CalendarAdapter
import com.example.gym_application.model.ClassWithScheduleModel
import com.example.gym_application.newModel.NewSchedule
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
@RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseDateTime(date: String, time: String): LocalDateTime? {
        return try {
            LocalDateTime.parse("$date$time", DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm"))
        } catch (e: DateTimeParseException) {
            null
        }
    }



    fun convertTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return (hours * 60) + minutes
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isClassOverForSchedules(inClassDate: String, inClassEndTime : String): Boolean {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        return try {
            val classDate = LocalDate.parse(inClassDate, dateFormatter)
            val classEndTime = LocalTime.parse(inClassEndTime, timeFormatter)
            val classDateTimeEnd = classDate.atTime(classEndTime)

            val now = LocalDateTime.now()
            val isOver = now.isAfter(classDateTimeEnd)
            isOver
        } catch (e: Exception) {
            true
        }
    }


    object Order {
        @RequiresApi(Build.VERSION_CODES.O)
        fun orderSchedulesInOrder(scheduleList: List<NewSchedule>): List<NewSchedule> {
            val dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val now = LocalDateTime.now()

            return scheduleList.sortedWith { a, b ->
                val aDateTime = parseDateTime(a.classStartDate, a.classStartTime, dateTimeFormat)
                val bDateTime = parseDateTime(b.classStartDate, b.classStartTime, dateTimeFormat)

                when {
                    aDateTime.isAfter(now) && bDateTime.isBefore(now) -> -1
                    aDateTime.isBefore(now) && bDateTime.isAfter(now) -> 1
                    else -> aDateTime.compareTo(bDateTime)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun parseDateTime(date: String, time: String, formatter: DateTimeFormatter): LocalDateTime {
            return try {
                LocalDateTime.parse("$date $time", formatter)
            } catch (e: Exception) {
                LocalDateTime.MIN
            }
        }
    }


}