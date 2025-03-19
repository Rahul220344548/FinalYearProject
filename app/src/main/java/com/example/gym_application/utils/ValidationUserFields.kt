package com.example.gym_application.utils

object ValidationUserFields {

    fun validateUserFields(
        firstName: String,
        lastName: String,
        inDay : String,
        inMonth : String,
        inYear : String,
        phoneNumber : String,
        selectedGender: String,
        selectedRole : String

    ): String {
        if (!ValidationUtils.isValidFirstname(firstName)) {
            return "Please enter a valid first name (at least 2 characters)"
        }
        if(!ValidationUtils.isValidLastName(lastName)) {
            return "Please enter a valid last name (at least 2 characters)"
        }
        if (!ValidationUtils.isValidDay(inDay)) {
            return "Please enter a valid day of birth (1-31)"
        }
        if (!ValidationUtils.isValidMonth(inMonth)) {
            return "Please enter a valid month of birth (1-12)"
        }
        if (!ValidationUtils.isValidYear(inYear)) {
            return "Please enter a valid year of birth"
        }
        if (!ValidationUtils.isValidPhoneNumber(phoneNumber)) {
            return "Please enter a valid phone number (10-15 digits, can start with +)"
        }
        if (!ValidationUtils.isValidGenderdropdown(selectedGender)) {
            return "Please select a gender"
        }
        if (!ValidationUtils.isValidRolesdropdown(selectedRole)) {
            return "Please select a role"
        }

        return ""
    }
}