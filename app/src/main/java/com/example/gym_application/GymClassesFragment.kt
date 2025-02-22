package com.example.gym_application

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
class GymClassesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter
    private var currentMonth: LocalDate = LocalDate.now()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gym_classes, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = CalendarAdapter()
        recyclerView.adapter = adapter
        updateRecyclerView()
        return view
    }

    fun updateMonth(month: LocalDate) {
        currentMonth = month
        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        val daysInMonth = YearMonth.from(currentMonth).lengthOfMonth()
        val dates = (1..daysInMonth).map { day ->
            LocalDate.of(currentMonth.year, currentMonth.month, day)
        }
        adapter.submitList(dates)
    }
}