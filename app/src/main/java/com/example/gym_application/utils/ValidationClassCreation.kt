package com.example.gym_application.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object ValidationClassCreation {

    fun isValidClassTitle(title : String) : Boolean{
        return title.length >2
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

    fun isValidOccurrences(occurrences: List<String>): Boolean {
        return occurrences.isNotEmpty()
    }

    fun convertTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return (hours * 60) + minutes
    }

}