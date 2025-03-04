package com.example.gym_application.utils

import android.R
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView


fun utilsSetUpSelectColorDropdown(
    context: Context,
    colors : Array<String>,
    autoCompleteColorTextView : AutoCompleteTextView,
    selectedColor : (String) -> Unit,
    ){

    val arrayAdapter = ArrayAdapter(context, R.layout.simple_dropdown_item_1line, colors)
    autoCompleteColorTextView.setAdapter(arrayAdapter)

    autoCompleteColorTextView.setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position) as String
        selectedColor(selected)
    }

}