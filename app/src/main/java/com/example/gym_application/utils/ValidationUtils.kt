package com.example.gym_application.utils

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phonePattern = "^[+]?[0-9]{10,15}$"
        return phoneNumber.matches(phonePattern.toRegex())
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}
