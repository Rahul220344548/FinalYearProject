package com.example.gym_application.model

data class ClassModel(
    val classTitle: String = "",
    val classDescription: String = "",
    val classColor: String = "",
    val classLocation: String = "",
    val classInstructor: String = "",
    val classMaxCapacitiy: Int,
    val classGenderRestrictuons: String = ""
)