package com.example.gym_application.staff

import GymClassesAdapter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.CalendarAdapter
import com.example.gym_application.R
import com.example.gym_application.controller.ScheduleFirebaseHelper
import com.example.gym_application.utils.CalendarUtils
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)

class StaffSchedulesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter
    private lateinit var classAdapter: GymClassesAdapter
    private var currentMonth: LocalDate = LocalDate.now()

    private val scheduleFirebaseHelper = ScheduleFirebaseHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_staff_schedules, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val classesRecyclerView: RecyclerView =  view.findViewById(R.id.classesRecyclerView)
        classesRecyclerView.layoutManager = LinearLayoutManager(context)
        classAdapter = GymClassesAdapter(emptyList())
        classesRecyclerView.adapter = classAdapter

        adapter = CalendarAdapter { selectedDate ->
            val formattedDate = CalendarUtils.formatDate(selectedDate.toString())
            scheduleFirebaseHelper.removeScheduleListener()
            val scheduleRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")
            scheduleFirebaseHelper.fetchSchedulesForDateLive(scheduleRef, formattedDate) { classList ->
                classAdapter.updateData(classList)
            }
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



}