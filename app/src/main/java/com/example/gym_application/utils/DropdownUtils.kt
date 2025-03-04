package com.example.gym_application.utils

import android.R
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView


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