package com.example.gym_application.model

data class Schedule(
    val scheduleId: String = "",
    val classId : String = "",
    val classStartTime: String = "",
    val classEndTime: String = "",
    val classStartDate: String = "",
    val classCurrentBookings : Int = 0,
    val status : String = "active",
)