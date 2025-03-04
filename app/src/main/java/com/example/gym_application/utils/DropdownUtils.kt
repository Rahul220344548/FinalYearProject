package com.example.gym_application.utils

import android.R
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
    instructorList: List<String>,
    autoCompleteInstructorTextView : AutoCompleteTextView,
    selectedInstructor: (String) -> Unit,
) {

    val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, instructorList)
    autoCompleteInstructorTextView.setAdapter(arrayAdapter)

    autoCompleteInstructorTextView.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
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