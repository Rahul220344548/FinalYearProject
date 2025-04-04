package com.example.gym_application.utils

import GymClassesAdapter
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.gym_application.CalendarAdapter
import com.example.gym_application.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
object CalendarUtils {

    fun setupCalendarNavigation(
        view: View,
        currentMonthProvider: () -> LocalDate,
        setCurrentMonth: (LocalDate) -> Unit,
        adapter: CalendarAdapter,
    ) {
        val ivCalendarPrevious: ImageView = view.findViewById(R.id.iv_calendar_previous)
        val ivCalendarNext: ImageView = view.findViewById(R.id.iv_calendar_next)
        val textDateMonth: TextView = view.findViewById(R.id.text_date_month)


        fun updateMonthDisplay(currentMonth: LocalDate) {
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
            textDateMonth.text = currentMonth.format(formatter)
        }

        fun updateRecyclerView(currentMonth: LocalDate) {
            val today = LocalDate.now()
            val daysInMonth = YearMonth.from(currentMonth).lengthOfMonth()
            val dates = (1..daysInMonth).map { day ->
                LocalDate.of(currentMonth.year, currentMonth.month, day)
            }.filter { it.isEqual(today) || it.isAfter(today) }
            adapter.submitList(dates)
        }

        ivCalendarPrevious.setOnClickListener {
            val previousMonth = currentMonthProvider().minusMonths(1)
            val now = LocalDate.now().withDayOfMonth(1)

            if (!previousMonth.isBefore(now)) {
                setCurrentMonth(previousMonth)
                updateMonthDisplay(previousMonth)
                updateRecyclerView(previousMonth)
            }
        }

        ivCalendarNext.setOnClickListener {
            val nextMonth = currentMonthProvider().plusMonths(1)
            setCurrentMonth(nextMonth)
            updateMonthDisplay(nextMonth)
            updateRecyclerView(nextMonth)
        }

        val initialMonth = currentMonthProvider()
        updateMonthDisplay(initialMonth)
        updateRecyclerView(initialMonth)
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
}