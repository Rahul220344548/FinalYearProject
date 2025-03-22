package com.example.gym_application.utils

object ValidationClassScheduleFields {

    fun validationClassScheduleFields(
        selectedClassName : String,
        startTime: String,
        endTime: String,
        startDate: String
    ): String {

        if (!ValidationClassCreation.isValidSelectClassOptionName(selectedClassName)) {
            return "Please select an Class Option"
        }

        if (!ValidationClassCreation.isValidTime(startTime, endTime)) {
            return "Invalid time: Class duration must be 30 minutes or 1 hour"
        }

        if (!ValidationClassCreation.isValidScheduledDate(startDate)) {
            return "Please pick a schedule date"
        }
        return ""
    }

}