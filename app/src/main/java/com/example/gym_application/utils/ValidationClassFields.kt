package com.example.gym_application.utils

object ValidationClassFields {

    fun validateClassFields(
        title: String,
        description: String,
        capacity: String,
        selectedGenderId: Int,
        selectedColor: String,
        selectedRoom: String,
        selectedInstructor: String
    ): String {
        if (!ValidationClassCreation.isValidClassTitle(title)) {
            return "Please enter a class Title (at least 3 characters)"
        }

        if (!ValidationClassCreation.isValidClassDescription(description)) {
            return "Class description must be between 1 and 120 words"
        }

        if (!ValidationClassCreation.isValidClassColor(selectedColor)) {
            return "Please select a class color"
        }

        if (!ValidationClassCreation.isValidSelectRoom(selectedRoom)) {
            return "Please select a Room"
        }

        if (!ValidationClassCreation.isValidSelectInstructor(selectedInstructor)) {
            return "Please select an Instructor"
        }

        if (!ValidationClassCreation.isValidCapacity(capacity)) {
            return "Please enter a valid class limit ( must be < 20 )"
        }

        if (!ValidationClassCreation.isValidGenderSelection(selectedGenderId)) {
            return "Please select a gender restriction"
        }

//        if (!ValidationClassCreation.isValidTime(startTime, endTime)) {
//            return "Invalid time: Class duration must be 30 minutes or 1 hour"
//        }
//
//        if (!ValidationClassCreation.isValidScheduledDate(startDate)) {
//            return "Please pick a schedule date"
//        }

        return ""
    }
}
