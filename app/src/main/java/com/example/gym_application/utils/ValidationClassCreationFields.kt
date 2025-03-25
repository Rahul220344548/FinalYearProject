package com.example.gym_application.utils

object ValidationClassCreationFields {

    fun validationClassCreationFields(
        title: String,
        description: String,
        selectedColor: String,
        capacity: String,
        availabilityFor: String
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
        if (!ValidationClassCreation.isValidCapacity(capacity)) {
            return "Please enter a valid class limit ( must be < 20 )"
        }

        if (!ValidationClassCreation.isValidClassColor(availabilityFor)) {
            return "Please select a class avaliability option "
        }

        return ""
    }
}
