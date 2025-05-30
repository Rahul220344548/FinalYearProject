package com.example.gym_application.utils

object ValidationUtils {

    fun isValidFirstname( firstname : String) : Boolean {
        return firstname.isNotEmpty() && firstname.length >=2
    }

    fun isValidLastName ( lastname : String) : Boolean {
        return lastname.isNotEmpty() && lastname.length >=2
    }

    fun isValidDay ( day: String) : Boolean {
        return day.isNotEmpty() && day.toIntOrNull() in 1..31
    }

    fun isValidMonth(month: String): Boolean {
        return month.isNotEmpty() && month.toIntOrNull() in 1..12
    }
    // Validates that the year is not empty and is between 1900 and the current year
    fun isValidYear(year: String): Boolean {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        return year.isNotEmpty() && year.toIntOrNull() in 1900..currentYear
    }

    // // Combines the day, month, and year checks, and also ensures the date is not in the future
    fun isValidDateOfBirth(day: String, month: String, year: String): Boolean {
        if (!isValidDay(day) || !isValidMonth(month) || !isValidYear(year)) return false

        val dobCalendar = java.util.Calendar.getInstance()
        dobCalendar.set(year.toInt(), month.toInt() - 1, day.toInt())

        val today = java.util.Calendar.getInstance()
        return !dobCalendar.after(today)
    }

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    //checks if the phone number matches a pattern for 10 to 15 digits (with optional + sign)
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phonePattern = "^[+]?[0-9]{10,15}$"
        return phoneNumber.matches(phonePattern.toRegex())
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun isValidGender(gender: Int): Boolean {
        return gender != -1
    }
    // checks if gender is selected from dropdown
    fun isValidGenderdropdown(selectedGender : String?):Boolean {
        return !selectedGender.isNullOrEmpty()
    }
    fun isValidRolesdropdown(selectedRoles : String?):Boolean {
        return !selectedRoles.isNullOrEmpty()
    }
}
