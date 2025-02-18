package com.example.gym_application.admin_view.navigation_fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.FragmentManager
import com.example.gym_application.HomeActivity
import com.example.gym_application.R
import com.example.gym_application.SignUp
import com.example.gym_application.admin_view.CreateAClass
import com.example.gym_application.admin_view.classes_fragments.FragmentBookingList
import com.example.gym_application.admin_view.classes_fragments.FragmentClassList
import com.example.gym_application.admin_view.classes_fragments.FragmentScheduleList


class AdminClassesFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_classes, container, false)
//        return inflater.inflate(R.layout.fragment_admin_classes, container, false)

        val btnClassList: AppCompatButton = view.findViewById(R.id.btnClassList)
        val btnScheduleList: AppCompatButton= view.findViewById(R.id.btnScheduleList)
        val btnBookingList: AppCompatButton = view.findViewById(R.id.btnBookingList)

        btnClassList.setOnClickListener {
            goToFragment(FragmentClassList())
        }

        btnScheduleList.setOnClickListener {
            goToFragment(FragmentScheduleList())
        }

        btnBookingList.setOnClickListener {
            goToFragment(FragmentBookingList())
        }

        val addClassBtn: Button = view.findViewById(R.id.btnAddClass)

        addClassBtn.setOnClickListener {
            goToCreateAClassPage()
        }
        return view

    }
    private fun goToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_buttonContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
    fun goToCreateAClassPage() {
        val intent = Intent(requireActivity(), CreateAClass::class.java)
        startActivity(intent)
    }
}