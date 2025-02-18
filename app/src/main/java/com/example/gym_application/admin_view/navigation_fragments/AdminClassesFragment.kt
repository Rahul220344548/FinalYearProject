package com.example.gym_application.admin_view.navigation_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.example.gym_application.HomeActivity
import com.example.gym_application.R
import com.example.gym_application.SignUp
import com.example.gym_application.admin_view.CreateAClass


class AdminClassesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_classes, container, false)
//        return inflater.inflate(R.layout.fragment_admin_classes, container, false)

        val addClassBtn: Button = view.findViewById(R.id.btnAddClass)

        addClassBtn.setOnClickListener {
            goToCreateAClassPage()
        }
        return view

    }

    fun goToCreateAClassPage() {
        val intent = Intent(requireActivity(), CreateAClass::class.java)
        startActivity(intent)
    }
}