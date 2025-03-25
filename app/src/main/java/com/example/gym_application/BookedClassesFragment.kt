package com.example.gym_application

import FirebaseDatabaseHelper
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.controller.BookedClassesAdapter
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.model.ClassWithScheduleModel
import com.google.firebase.auth.FirebaseAuth


class BookedClassesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var classAdapter : BookedClassesAdapter
    private lateinit var txtMessageNoBookings : TextView

    private val userFirebaseHelper = UserFirebaseDatabaseHelper()
    private val classFirebaseHelper = FirebaseDatabaseHelper()

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_booked_classes, container, false)


        recyclerView = view.findViewById(R.id.bookedClassesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        classAdapter = BookedClassesAdapter(emptyList())
        recyclerView.adapter = classAdapter

        txtMessageNoBookings = view.findViewById<TextView>(R.id.txtNoBookings)

        fetchUserCurrentBookings()

        return view
    }


    private fun fetchUserCurrentBookings() {
        userFirebaseHelper.fetchUserCurrentBookings(userId?:"") { fetchedClassId ->
            if (fetchedClassId != null) {
                fetchClassDetails(fetchedClassId)
                txtMessageNoBookings.visibility = View.GONE
            } else {
                Log.d("FetchUserCurrentBookings", "No current class booking found")
                recyclerView.visibility = View.GONE
            }
        }
    }

    private fun fetchClassDetails(classIds : List<String>) {

        val classDetailsList = mutableListOf<ClassWithScheduleModel>()

        classIds.forEach { classId ->
            classFirebaseHelper.getClassesFullDetails(classId) { classDetails ->
                if (classDetails != null) {
                    txtMessageNoBookings.visibility = View.GONE
                    classDetailsList.add(classDetails)
                    classAdapter.updateData(classDetailsList)
                }
            }
        }
    }



}