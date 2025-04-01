package com.example.gym_application.utils

object ValidationMembershipCreationFields {

    fun validationMembershipCreationFields(
        title: String,
        seletedDuration : String,
        price : String,
    ): String {

        if(!ValidationClassCreation.isValidClassTitle(title)){
            return "Please enter a membership title (at least 3 characters)"
        }
        if (!ValidationClassCreation.isValidClassColor(seletedDuration)) {
            return "Please select a membership duration"
        }
        if (!ValidationClassCreation.isValidMembershipPrice(price)) {
            return "Please enter a valid membership price ( > 20 )"
        }
        return ""
    }


}