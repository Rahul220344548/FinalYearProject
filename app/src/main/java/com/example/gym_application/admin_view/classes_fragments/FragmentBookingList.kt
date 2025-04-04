package com.example.gym_application.admin_view.classes_fragments

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
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminAllScheduleListAdapter
import com.example.gym_application.admin_view.adapter.AdminClassListAdapter
import com.example.gym_application.controller.AllUserBookingsAdapter
import com.example.gym_application.controller.ScheduleFirebaseHelper
import com.example.gym_application.model.ClassTemplate
import com.example.gym_application.newModel.NewSchedule
import com.example.gym_application.utils.DialogUtils
import com.example.gym_application.utils.formatDateUtils
import com.example.gym_application.utils.formatDateUtils.Order.orderSchedulesInOrder


class FragmentBookingList : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : AdminAllScheduleListAdapter

    private val scheduleFirebaseHelper = ScheduleFirebaseHelper()
    private var scheduleList : List<NewSchedule> = emptyList()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_booking_list, container, false)

        recyclerView = view.findViewById(R.id.adminAllSchedulesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdminAllScheduleListAdapter(emptyList())
        recyclerView.adapter = adapter

        fetchAllSchedulesList()

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchAllSchedulesList() {
        scheduleFirebaseHelper.listenForScheduleUpdates { fetchedList ->
            if (fetchedList.isNotEmpty()) {
                scheduleList = orderSchedulesInOrder(fetchedList)
                adapter.updateData(scheduleList)
            }else {
                Toast.makeText(context, "Error ", Toast.LENGTH_SHORT).show()
            }
        }
    }






}