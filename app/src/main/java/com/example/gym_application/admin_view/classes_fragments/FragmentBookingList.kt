package com.example.gym_application.admin_view.classes_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminClassListAdapter


class FragmentBookingList : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking_list, container, false)



    }

    private fun fetchAllClasses() {

    }



}