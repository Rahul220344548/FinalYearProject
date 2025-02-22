package com.example.gym_application

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CalendarAdapter : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var dates: List<LocalDate> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_layout, parent, false)
        return CalendarViewHolder(view)
    }


    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = dates[position]
        holder.bind(date)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    fun submitList(newDates: List<LocalDate>) {
        dates = newDates
        notifyDataSetChanged()
    }

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCalendarDate: TextView = itemView.findViewById(R.id.tv_calendar_date)
        private val tvCalendarDay: TextView = itemView.findViewById(R.id.tv_calendar_day)


        fun bind(date: LocalDate) {
            val dayFormatter = DateTimeFormatter.ofPattern("dd")
            val weekFormatter = DateTimeFormatter.ofPattern("EEE")
            tvCalendarDate.text = date.format(dayFormatter)
            tvCalendarDay.text = date.format(weekFormatter)
        }
    }
}