package com.example.gym_application.admin_view.classes_fragments

import FirebaseDatabaseHelper
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminBookingListAdapter


class FragmentClassList : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : AdminBookingListAdapter

    private val classFirebaseHelper = FirebaseDatabaseHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_class_list, container, false)

        recyclerView = view.findViewById(R.id.adminClassesListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = AdminBookingListAdapter(emptyList())
        recyclerView.adapter = adapter

        fetchClassesList()

        return view

    }

    private fun fetchClassesList() {

        classFirebaseHelper.getAllClasses { classList ->
            if (classList.isNotEmpty()) {
                adapter.updateData(classList)
            }else {
                println("Error fetching classes")
            }
        }

    }

}