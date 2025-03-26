package com.example.gym_application.model

data class UserClassBooking(
    val classId: String,
    val scheduleId: String

)
{
    constructor(): this("","")
}
