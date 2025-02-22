package com.example.gym_application.model
import com.google.firebase.database.IgnoreExtraProperties

data class ClassModel(
    val classTitle: String = "",
    val classDescription: String = "",
    val classColor: String = "",
    val classLocation: String = "",
    val classInstructor: String = "",
    val classMaxCapacitiy: Int,
    val classGenderRestrictuons: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val startAvailability: String = "",
    val endAvailability: String = "",
    val occurrences: List<String> = listOf()
) {

    constructor() : this("", "", "", "", "", 0, "", "", "", "", "", listOf())
}