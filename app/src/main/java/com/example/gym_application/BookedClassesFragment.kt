package com.example.gym_application

import FirebaseDatabaseHelper
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.controller.BookedClassesAdapter
import com.example.gym_application.controller.ScheduleFirebaseHelper
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.model.ClassWithScheduleModel
import com.example.gym_application.model.Schedule
import com.example.gym_application.newModel.NewSchedule
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
class BookedClassesFragment : Fragment() {

    override fun onDestroyView() {
        super.onDestroyView()
        scheduleFirebaseHelper.removeAllScheduleListeners()
        userFirebaseHelper.removeUserCurrentBookingsListener(userId ?: "")
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var classAdapter : BookedClassesAdapter
    private lateinit var txtMessageNoBookings : TextView

    private val userFirebaseHelper = UserFirebaseDatabaseHelper()
    private val classFirebaseHelper = FirebaseDatabaseHelper()
    private val scheduleFirebaseHelper = ScheduleFirebaseHelper()

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

        observeUserCurrentBookingsLive()

        return view
    }


    private fun observeUserCurrentBookingsLive() {
        userFirebaseHelper.listenToUserCurrentBookingsLive(userId ?: "") { bookings ->
            if (bookings.isNotEmpty()) {
                txtMessageNoBookings.visibility = View.GONE
                fetchClassDetails(bookings)
            }
        }
    }

    private fun fetchClassDetails(scheduleIds: List<String>) {
        val liveSchedules = mutableListOf<NewSchedule>()
        scheduleIds.forEach { scheduleId ->
            scheduleFirebaseHelper.listenToBookedSchedulesFullDetail(scheduleId) { updatedSchedule ->
                if (updatedSchedule != null && updatedSchedule.status == "active") {
                    val existingIndex = liveSchedules.indexOfFirst { it.scheduleId == updatedSchedule.scheduleId }
                    if (existingIndex >= 0) {
                        liveSchedules[existingIndex] = updatedSchedule
                    } else {
                        liveSchedules.add(updatedSchedule)
                    }
                    classAdapter.updateData(liveSchedules)
                }
            }
        }
    }



}