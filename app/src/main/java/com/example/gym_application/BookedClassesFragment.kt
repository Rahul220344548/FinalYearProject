package com.example.gym_application

import FirebaseDatabaseHelper
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.controller.BookedClassesAdapter
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.model.ClassModel
import com.google.firebase.auth.FirebaseAuth


class BookedClassesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var classAdapter : BookedClassesAdapter

    private val firebaseHelper = UserFirebaseDatabaseHelper()
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


        fetchUserCurrentBookings()

//        fetchClassDetails("-OKCm0jGOVLNWc_AYLKV")

        return view
    }


    private fun fetchUserCurrentBookings() {


        firebaseHelper.getUserCurrentBookings(userId?:"") { fetchedClassId ->

            if (fetchedClassId != null) {
                Log.d("FetchUserCurrentBookings", "Fetched Class ID: $fetchedClassId")
                fetchClassDetails(fetchedClassId)
            } else {
                Log.d("FetchUserCurrentBookings", "No current class booking found")

            }
        }


    }



    private fun fetchClassDetails(classId:String) {

        classFirebaseHelper.getClassesFullDetails(classId ?: "") { classdetails ->

            if (classdetails != null) {
                println("$classdetails")

            }else {
                println("Nothing Found")
            }

        }



    }



}