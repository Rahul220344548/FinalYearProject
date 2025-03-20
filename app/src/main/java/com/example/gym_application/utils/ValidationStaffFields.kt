package com.example.gym_application.utils

object ValidationStaffFields {

    fun validateStaffFields(
        staffFirstName: String,
        staffLastName: String,
        staffDOBSelectedGender: String,
        staffSelectedRole : String,
        staffDOBDay : String,
        staffDOBMonth : String,
        staffDOBYear : String,
        staffPhoneNumber : String,
        staffEmailAddress : String,
        staffPassword : String,
        staffReEnterPassword: String

    ): String {
        if (!ValidationUtils.isValidFirstname(staffFirstName)) {
            return "Please enter a valid first name (at least 2 characters)"
        }

        if (!ValidationUtils.isValidLastName(staffLastName)) {
            return "Please enter a valid last name (at least 2 characters)"
        }

        if (!ValidationUtils.isValidGenderdropdown(staffDOBSelectedGender)) {
            return "Please select a gender"
        }

        if (!ValidationUtils.isValidRolesdropdown(staffSelectedRole)) {
            return "Please select a role"
        }

        if (!ValidationUtils.isValidDay(staffDOBDay)) {
            return "Please enter a valid day of birth (1-31)"
        }

        if (!ValidationUtils.isValidMonth(staffDOBMonth)) {
            return "Please enter a valid month of birth (1-12)"
        }

        if (!ValidationUtils.isValidYear(staffDOBYear)) {
            return "Please enter a valid year of birth"
        }

        if (!ValidationUtils.isValidDateOfBirth(staffDOBDay,staffDOBMonth,staffDOBYear)) {
            return "Invalid date of birth. Ensure it's a past date."
        }

        if (!ValidationUtils.isValidPhoneNumber(staffPhoneNumber)) {
            return "Please enter a valid phone number (10-15 digits, can start with +)"
        }


        if (!ValidationUtils.isValidEmail(staffEmailAddress)) {
            return "Please enter a valid email address (e.g., user@example.com)"
        }

        if (!ValidationUtils.isValidPassword(staffPassword)) {
            return "Password must be at least 6 characters long"
        }

        if (!ValidationUtils.doPasswordsMatch(staffPassword, staffReEnterPassword)) {
            return "Passwords do not match"
        }

        return ""
    }
}