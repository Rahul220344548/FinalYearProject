package com.example.gym_application.staff

import GymClassesAdapter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.CalendarAdapter
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminScheduleListAdapter
import com.example.gym_application.controller.ScheduleFirebaseHelper
import com.example.gym_application.model.Schedule
import com.example.gym_application.newModel.NewSchedule
import com.example.gym_application.utils.CalendarUtils
import com.example.gym_application.utils.DialogUtils
import com.example.gym_application.utils.formatDateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)

class StaffSchedulesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter
    private lateinit var classAdapter: StaffSchedulesAdapter
    private var currentMonth: LocalDate = LocalDate.now()

    private val scheduleFirebaseHelper = ScheduleFirebaseHelper()
    private val instructorId = getCurrentUserId().toString()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_staff_schedules, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val classesRecyclerView: RecyclerView =  view.findViewById(R.id.classesRecyclerView)
        classesRecyclerView.layoutManager = LinearLayoutManager(context)
        classAdapter = StaffSchedulesAdapter(emptyList())
        classesRecyclerView.adapter = classAdapter

        adapter = CalendarAdapter { selectedDate ->
            fetchSchedulesForDate(instructorId, selectedDate.toString())
        }

        recyclerView.adapter = adapter

        CalendarUtils.setupCalendarNavigation(
            view = view,
            currentMonthProvider = { currentMonth },
            setCurrentMonth = { newMonth -> currentMonth = newMonth },
            adapter = adapter
        )

        return view

    }

    fun fetchSchedulesForDate(instructorId: String, date: String) {
        val dateRef = FirebaseDatabase.getInstance().getReference("users")
            .child(instructorId)
            .child("mySchedules")
            .child(date)

        dateRef.get().addOnSuccessListener { snapshot ->
            val scheduleIds = mutableListOf<String>()
            for (child in snapshot.children) {
                val scheduleId = child.key
                if (scheduleId != null) {
                    scheduleIds.add(scheduleId)
                }
            }
            if (scheduleIds.isEmpty()) {
                classAdapter.updateData(emptyList())
            } else {
                loadSchedulesByIds(scheduleIds)
            }


        }.addOnFailureListener {
            Toast.makeText(context, "Failed to load schedules.", Toast.LENGTH_SHORT).show()
        }
    }


    fun loadSchedulesByIds(scheduleIds: List<String>) {
        val schedules = mutableListOf<NewSchedule>()

        val schedulesInfoRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")

        for (scheduleId in scheduleIds) {
            schedulesInfoRef.child(scheduleId).get().addOnSuccessListener { snapshot ->
                val schedule = snapshot.getValue(NewSchedule::class.java)
                if (schedule != null && schedule.status == "active") {
                    schedules.add(schedule)

                    if (schedules.size == scheduleIds.size) {
                        classAdapter.updateData(schedules)
                    }
                }
            }
        }
    }

    private fun getCurrentUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

}