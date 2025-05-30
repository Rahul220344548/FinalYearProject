package com.example.gym_application.utils

import android.R
import android.app.DatePickerDialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.example.gym_application.newModel.Instructor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


fun utilsSetUpSelectColorDropdown(
    context: Context,
    autoCompleteColorTextView : AutoCompleteTextView,
    selectedColor : (String) -> Unit,
    ){

    val colors = context.resources.getStringArray(com.example.gym_application.R.array.colors)
    val arrayAdapter = ArrayAdapter(context, R.layout.simple_dropdown_item_1line, colors)
    autoCompleteColorTextView.setAdapter(arrayAdapter)

    autoCompleteColorTextView.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
        selectedColor(selected)
    }

}

fun utilsSetUpSelectAvailabilityForDropdown(
    context: Context,
    autoCompleteAvailabilityFor : AutoCompleteTextView,
    selectedAvailability: (String) -> Unit,
){
    val options = context.resources.getStringArray(com.example.gym_application.R.array.availabilityFor)
    val arrayAdapter = ArrayAdapter(context, R.layout.simple_dropdown_item_1line, options)
    autoCompleteAvailabilityFor.setAdapter(arrayAdapter)

    autoCompleteAvailabilityFor.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
        selectedAvailability(selected)
    }



}

fun utilsSetUpSelectRoomDropdown(
    context: Context,
    autoCompleteRoomTextView : AutoCompleteTextView,
    selectedRoom : (String) -> Unit,
    ) {
    val rooms = context.resources.getStringArray(com.example.gym_application.R.array.rooms)
    val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, rooms)
    autoCompleteRoomTextView.setAdapter(arrayAdapter)

    autoCompleteRoomTextView.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
        selectedRoom(selected)
    }
}

fun utilsSetUpSelectInstructorDropdown(
    context: Context,
    instructorList: List<Instructor>,
    autoCompleteInstructorTextView : AutoCompleteTextView,
    selectedInstructor: (Instructor) -> Unit,
) {

    val namesList = instructorList.map { it.name }
    val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, namesList)
    autoCompleteInstructorTextView.setAdapter(arrayAdapter)

    autoCompleteInstructorTextView.setOnItemClickListener { parent, _, position, _ ->
        val selected = instructorList[position]
        selectedInstructor(selected)
    }

}

fun utilsSetUpStartTimeDropdown(
    context: Context,
    autoCompleteStartTime: AutoCompleteTextView
){

    val timeSlots = mutableListOf<String>()
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 9)
    calendar.set(Calendar.MINUTE, 0)

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    while (calendar.get(Calendar.HOUR_OF_DAY) < 20 ||
        (calendar.get(Calendar.HOUR_OF_DAY) == 20 && calendar.get(Calendar.MINUTE) == 30)) {
        timeSlots.add(timeFormat.format(calendar.time))
        calendar.add(Calendar.MINUTE, 30)
    }

    val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, timeSlots)
    autoCompleteStartTime.setAdapter(adapter)

    autoCompleteStartTime.setOnItemClickListener { parent, _, position, _ ->
        autoCompleteStartTime.setText(parent.getItemAtPosition(position) as String, false)
    }

}

fun utilsSetUpEndTimeDropdown(
    context: Context,
    autoCompleteEndTime: AutoCompleteTextView
){

    val timeSlots = mutableListOf<String>()
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 9)
    calendar.set(Calendar.MINUTE, 30)

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    while (calendar.get(Calendar.HOUR_OF_DAY) < 20 ||
        (calendar.get(Calendar.HOUR_OF_DAY) == 20 && calendar.get(Calendar.MINUTE) == 0)) {
        timeSlots.add(timeFormat.format(calendar.time))
        calendar.add(Calendar.MINUTE, 30)
    }

    val adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, timeSlots)
    autoCompleteEndTime.setAdapter(adapter)

    autoCompleteEndTime.setOnItemClickListener { parent, _, position, _ ->
        autoCompleteEndTime.setText(parent.getItemAtPosition(position) as String, false)
    }


}

fun utilsSetUpClassScheduleDate(
    context: Context,
    startDate : AutoCompleteTextView
) {

    startDate.setOnClickListener {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                if (selectedCalendar.before(calendar)) {
                    Toast.makeText(context, "End Date cannot be in the past!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val selectedDate = String.format(
                        Locale.getDefault(),
                        "%02d/%02d/%d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )
                    startDate.setText(selectedDate)
                }
            }, year, month, day)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()

    }

}

fun utilsSetUpSelectUserGender(
    context: Context,
    autoCompleteGenderTextView : AutoCompleteTextView,
    selectedGender : (String) -> Unit,
) {

    val genders = context.resources.getStringArray(com.example.gym_application.R.array.genders)
    val arrayAdapter = ArrayAdapter(context,android.R.layout.simple_dropdown_item_1line, genders)
    autoCompleteGenderTextView.setAdapter(arrayAdapter)

    autoCompleteGenderTextView.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
        selectedGender(selected)
    }
}

fun utilsSetUpSelectUserRoles(
    context: Context,
    autoCompleteRoleTextView : AutoCompleteTextView,
    selectedRoles : (String) -> Unit,
) {

    val roles = context.resources.getStringArray(com.example.gym_application.R.array.roles)
    val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, roles)
    autoCompleteRoleTextView.setAdapter(arrayAdapter)

    autoCompleteRoleTextView.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
        selectedRoles(selected)
    }


}

fun utilsSetUpSelectActivateUser(
    context: Context,
    autoCompleteActivationTextView : AutoCompleteTextView,
    selectedStatus : (String) -> Unit,
){
    val status = context.resources.getStringArray(com.example.gym_application.R.array.statuses)
    val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, status)
    autoCompleteActivationTextView.setAdapter(arrayAdapter)

    autoCompleteActivationTextView.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
        selectedStatus(selected)
    }
}



fun utilsSetUpSelectClassesOptionsDropdown(
    context: Context,
    classList: List<String>,
    classOption : AutoCompleteTextView,
    selectedClasses: (String) -> Unit
) {
    val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, classList)
    classOption.setAdapter(arrayAdapter)

    classOption.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
        selectedClasses(selected)
    }
}

fun utilsSetUpSelectMembershipDurationDropdown(
    context: Context,
    autoCompleteDuration: AutoCompleteTextView,
    selectedDuration: (String) -> Unit
){


    val durations = context.resources.getStringArray(com.example.gym_application.R.array.duration)
    val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, durations)
    autoCompleteDuration.setAdapter(arrayAdapter)

    autoCompleteDuration.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
        selectedDuration(selected)
    }

}