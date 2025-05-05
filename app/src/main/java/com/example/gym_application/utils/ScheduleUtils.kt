package com.example.gym_application.utils

import com.example.gym_application.model.ClassTemplate
import com.example.gym_application.newModel.NewSchedule
import com.google.firebase.database.FirebaseDatabase

object ScheduleUtils {

    fun generateNewScheduleId(): String? {
        val databaseRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")
        return databaseRef.push().key
    }

    fun createScheduleFromClassTemplate(
        classTemplate: ClassTemplate,
        scheduleId: String,
        location: String,
        instructor: String,
        startDate: String,
        startTime: String,
        endTime: String
    ): NewSchedule {
        return NewSchedule(
            scheduleId = scheduleId,
            classId = classTemplate.classId,
            classTitle = classTemplate.classTitle,
            classDescription = classTemplate.classDescription,
            classColor = classTemplate.classColor,
            classMaxCapacity = classTemplate.classMaxCapacity,
            classAvailabilityFor = classTemplate.classAvailabilityFor,
            classLocation = location,
            classInstructor = instructor,
            classStartDate = startDate,
            classStartTime = startTime,
            classEndTime = endTime,
            classCurrentBookings = 0,
            status = "active"
        )
    }

}