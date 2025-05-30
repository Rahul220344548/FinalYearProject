package com.example.gym_application

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
import com.example.gym_application.adapter.ScheduleFirebaseHelper
import com.example.gym_application.utils.CalendarUtils
import com.google.firebase.database.FirebaseDatabase
import helper.FirebaseClassesHelper
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class GymClassesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter
    private lateinit var classAdapter: GymClassesAdapter

    private val scheduleFirebaseHelper = ScheduleFirebaseHelper()
    private val newSchedulesFirebaseHelper = FirebaseClassesHelper()
    private var currentMonth: LocalDate = LocalDate.now()

    override fun onDestroyView() {
        super.onDestroyView()
        scheduleFirebaseHelper.removeScheduleListener()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gym_classes, container, false)

        //  Setup horizontal calendar RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val classesRecyclerView: RecyclerView = view.findViewById(R.id.classesRecyclerView)
        classesRecyclerView.layoutManager = LinearLayoutManager(context)

        //  Initialize class adapter with empty list
        classAdapter = GymClassesAdapter(emptyList())
        classesRecyclerView.adapter = classAdapter


        /*
            - Initialize calendar adapter
            - Fetch class schedules from Firebase for selected date
            - Update the classes RecyclerView with the fetched data
         */
        adapter = CalendarAdapter { selectedDate ->
            val formattedDate = CalendarUtils.formatDate(selectedDate.toString())

            scheduleFirebaseHelper.removeScheduleListener()

            val scheduleRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")
            scheduleFirebaseHelper.fetchSchedulesForDateLive(scheduleRef, formattedDate) { classList ->
                classAdapter.updateData(classList)
            }
        }

        // Set up navigation for changing calendar months
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