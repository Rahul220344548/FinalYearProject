package com.example.gym_application.admin_view.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.gym_application.R


class AdminClassesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_admin_classes, container, false)

        val rootView = inflater.inflate(R.layout.fragment_admin_classes, container, false)

        val colors = resources.getStringArray(R.array.colors)

        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, colors)

        val autoCompleteTextView = rootView.findViewById<AutoCompleteTextView>(R.id.auto_complete_txt)

        autoCompleteTextView.setAdapter(arrayAdapter)

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedColor = parent.getItemAtPosition(position) as String
            // Do something with the selected color
        }

        return rootView



    }

}