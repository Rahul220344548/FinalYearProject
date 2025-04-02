package com.example.gym_application.utils

object FaqUtils {

   fun validationFields(question:String,answer:String): String {
        if (!ValidationClassCreation.isValidClassDescription(question)) {
            return "FAQ Question must be between 1 and 120 words"
        }
        if (!ValidationClassCreation.isValidClassDescription(answer)){
            return "FAQ Answer must be between 1 and 120 words"
        }
        return ""
    }
}