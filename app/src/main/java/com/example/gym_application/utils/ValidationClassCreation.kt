package com.example.gym_application.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.gym_application.model.ClassWithScheduleModel
import com.example.gym_application.newModel.NewSchedule
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale

object ValidationClassCreation {

    fun isValidClassTitle(title : String) : Boolean{
        return title.length >2  && !title.any { it.isDigit() }
    }

    fun isValidClassDescription(description: String): Boolean {
        if (description.isEmpty()) {
            return false
        }
        val wordCount = description.trim().split("\\s+".toRegex()).size
        return wordCount in 1..120
    }

    fun isValidClassColor(selectedColor: String?): Boolean {
        return !selectedColor.isNullOrEmpty()
    }

    fun isValidSelectRoom(selectedRoom: String?):Boolean {
        return !selectedRoom.isNullOrEmpty()
    }

    fun isValidSelectInstructor(selectedInstructor : String?):Boolean {
        return !selectedInstructor.isNullOrEmpty()
    }

    fun isValidCapacity(capacity: String): Boolean {
        val cap = capacity.toIntOrNull()
        return cap != null && cap > 0 && cap <= 20
    }

    fun isValidGenderSelection(selectedGenderId: Int): Boolean {
        return selectedGenderId != -1
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

    fun isValidScheduledDate(startDate: String) : Boolean{
        return startDate.isNotEmpty()
    }

    fun isValidEndDate(startDate: String, endDate: String): Boolean {
        if (endDate.isEmpty()) {
            return false
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return try {
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)

            if (start != null && end != null) {
                return !end.before(start)
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun isValidSelectClassOptionName(selectedClassName : String?):Boolean {
        return !selectedClassName.isNullOrEmpty()
    }

    fun isValidOccurrences(occurrences: List<String>): Boolean {
        return occurrences.isNotEmpty()
    }

    fun convertTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return (hours * 60) + minutes
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val outputFormatter = DateTimeFormatter.ofPattern("EEEE dd, MMMM")

        return try {

            val parsedDate = LocalDate.parse(inputDate, inputFormatter)

            parsedDate.format(outputFormatter)
        } catch (e: DateTimeParseException) {

            "Invalid date format"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isScheduleTimeInFuture(classDate: String, classTime: String): Boolean {

        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val classDateTime = LocalDateTime.parse("$classDate $classTime", formatter)
            val now = LocalDateTime.now()

            classDateTime.isAfter(now)
        } catch (e: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isClassUpcomingOrToday(classItem: NewSchedule): Boolean {
        return try {
            val formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formatterTime = DateTimeFormatter.ofPattern("HH:mm")

            val classDate = LocalDate.parse(classItem.classStartDate, formatterDate)
            val classTime = LocalTime.parse(classItem.classStartTime, formatterTime)

            val nowDate = LocalDate.now()
            val nowTime = LocalTime.now()

            when {
                classDate.isAfter(nowDate) -> true // Future class
                classDate.isEqual(nowDate) && classTime.isAfter(nowTime) -> true // Today but not yet started
                else -> false // Past class
            }
        } catch (e: Exception) {
            false
        }
    }

    fun isValidMembershipPrice(price : String ) :Boolean{
        val cap = price.toIntOrNull()
        return cap !=null && cap > 0
    }


}