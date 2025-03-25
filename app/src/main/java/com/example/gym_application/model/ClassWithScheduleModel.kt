package com.example.gym_application.model

data class ClassWithScheduleModel(
    val scheduleId: String = "",
    val classId: String = "",
    val classTitle: String = "",
    val classDescription: String = "",
    val classColor: String = "",
    val classMaxCapacity: Int = 0,
    val classAvailabilityFor: String = "",
    val classLocation: String = "",
    val classInstructor: String = "",
    val classStartTime: String = "",
    val classEndTime: String = "",
    val classStartDate: String = "",
    val classCurrentBookings: Int = 0,
    val status: String = "active"
)
