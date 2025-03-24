package com.example.gym_application.model

data class Schedule(
    val scheduleId: String = "",
    val classId : String = "",
    val classLocation: String = "",
    val classInstructor: String = "",
    val classStartTime: String = "",
    val classEndTime: String = "",
    val classStartDate: String = "",
    val classCurrentBookings : Int = 0,
    val status : String = "active",
)