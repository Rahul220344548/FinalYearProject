package com.example.gym_application.model

data class ClassModel(
    val classId: String = "",
    val classTitle: String = "",
    val classDescription: String = "",
    val classColor: String = "",
    val classLocation: String = "",
    val classInstructor: String = "",
    val classMaxCapacity: Int=0,
    val classCurrentBookings: Int=0,
    val classAvailabilityFor: String = "",
    val classStartTime: String = "",
    val classEndTime: String = "",
    val classStartDate: String = "",
) {

    constructor() : this("","", "", "", "", "", 0, 0, "", "", "","")
}