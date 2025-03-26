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
        userFirebaseHelper.fetchUserCurrentBookingsAsPairs(userId ?: "") { bookings ->
            if (!bookings.isNullOrEmpty()) {
                txtMessageNoBookings.visibility = View.GONE
                fetchClassDetails(bookings)
            } else {
                Log.d("FetchUserCurrentBookings", "No current class booking found")
            }
        }
    }


    private fun fetchClassDetails(bookings: List<Pair<String, String>>) {
        val classDetailsList = mutableListOf<ClassWithScheduleModel>()

        bookings.forEach { (classId, scheduleId) ->
            classFirebaseHelper.getClassWithSpecificSchedule(classId, scheduleId) { classDetail ->
                if (classDetail != null) {
                    classDetailsList.add(classDetail)
                    classAdapter.updateData(classDetailsList)
                }
            }
        }
    }



}