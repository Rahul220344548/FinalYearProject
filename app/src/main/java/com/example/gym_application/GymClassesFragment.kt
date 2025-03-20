package com.example.gym_application

import FirebaseDatabaseHelper
import GymClassesAdapter
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class GymClassesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter
    private lateinit var classAdapter: GymClassesAdapter

    private var currentMonth: LocalDate = LocalDate.now()
    //

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gym_classes, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val classesRecyclerView: RecyclerView = view.findViewById(R.id.classesRecyclerView)
        classesRecyclerView.layoutManager = LinearLayoutManager(context)
        classAdapter = GymClassesAdapter(emptyList())
        classesRecyclerView.adapter = classAdapter

        adapter = CalendarAdapter { selectedDate ->
            val formattedDate = formatDate(selectedDate.toString());

            FirebaseDatabaseHelper().getClassesForDate(formattedDate) { classList ->
                classAdapter.updateData(classList) // Update RecyclerView
            }

        }
        recyclerView.adapter = adapter

        val ivCalendarPrevious: ImageView = view.findViewById(R.id.iv_calendar_previous)
        val ivCalendarNext: ImageView = view.findViewById(R.id.iv_calendar_next)
        val textDateMonth: TextView = view.findViewById(R.id.text_date_month)

        ivCalendarPrevious.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            updateMonthDisplay(textDateMonth)
            updateRecyclerView()
        }

        ivCalendarNext.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            updateMonthDisplay(textDateMonth)
            updateRecyclerView()
        }

        updateMonthDisplay(textDateMonth)
        updateRecyclerView()
        return view
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }

    private fun updateMonthDisplay(textView: TextView) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        textView.text = currentMonth.format(formatter)
    }

    private fun updateRecyclerView() {
        val today = LocalDate.now()
        val daysInMonth = YearMonth.from(currentMonth).lengthOfMonth()
        val dates = (1..daysInMonth).map { day ->
            LocalDate.of(currentMonth.year, currentMonth.month, day)
        }.filter { it.isEqual(today) || it.isAfter(today) }
        adapter.submitList(dates)
    }



}