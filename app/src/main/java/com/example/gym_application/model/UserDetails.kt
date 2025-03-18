package com.example.gym_application.model

data class UserDetails(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val gender: String,
    val phoneNumber: String,
    val role: String
){
    constructor() : this("","","","","","")
}

