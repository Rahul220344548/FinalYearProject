package com.example.gym_application.model

data class UserScheduleBooking(
    val userId: String,
    val classId: String,
    val scheduleId: String
)
{
    constructor(): this("","","")
}
