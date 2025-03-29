package com.example.gym_application.admin_view.adapter

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.ClassDetailViewActivity
import com.example.gym_application.R
import com.example.gym_application.admin_view.AdminScheduleInfo
import com.example.gym_application.model.ClassWithScheduleModel
import com.example.gym_application.utils.formatDateUtils
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AdminScheduleListAdapter ( private var classList : List<ClassWithScheduleModel>) :
    RecyclerView.Adapter<AdminScheduleListAdapter.ViewHolder>(){

        class ViewHolder ( view : View) : RecyclerView.ViewHolder(view) {
            val classTitle: TextView = view.findViewById(R.id.recyAdminSchedulesClassTitle)
            val classDate : TextView = view.findViewById(R.id.recyAdminSchedulesClassDate)
            val classTimes : TextView = view.findViewById(R.id.recyAdminSchedulesClassTimes)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_schedules, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scheduledListItem = classList[position]
        val getFormatteddate = formatDateUtils.adminFormattedDate(scheduledListItem.classStartDate)

        holder.classTitle.text = scheduledListItem.classTitle
        holder.classDate.text = getFormatteddate
        holder.classTimes.text = "${scheduledListItem.classStartTime} - ${scheduledListItem.classEndTime}"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AdminScheduleInfo::class.java).apply {
                putExtra("scheduleId",scheduledListItem.scheduleId)
                putExtra("classId", scheduledListItem.classId)
                putExtra("classTitle", scheduledListItem.classTitle)
                putExtra("classStartDate", scheduledListItem.classStartDate)
                putExtra("classStartTime", scheduledListItem.classStartTime)
                putExtra("classEndTime", scheduledListItem.classEndTime)
                putExtra("classLocation", scheduledListItem.classLocation)
                putExtra("classAvailabilityFor", scheduledListItem.classAvailabilityFor)
                putExtra("classInstructor",scheduledListItem.classInstructor)
                putExtra("classCurrentBookings", scheduledListItem.classCurrentBookings)
                putExtra("classMaxCapacity", scheduledListItem.classMaxCapacity)
                putExtra("classDescription", scheduledListItem.classDescription)
            }
            holder.itemView.context.startActivity(intent)
        }



    }

    override fun getItemCount(): Int = classList.size


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDate(newSchedule: List<ClassWithScheduleModel>) {

        val now = java.time.LocalTime.now()
        val today = java.time.LocalDate.now()

        val filteredList = newSchedule.filter { classItem ->
            val classDate = LocalDate.parse(classItem.classStartDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            if (classDate.isAfter(today)) {
                // displays future classes
                true
            } else if (classDate.isEqual(today)) {
                val classTime = LocalTime.parse(classItem.classStartTime, DateTimeFormatter.ofPattern("HH:mm"))
                classTime.isAfter(now)
            } else {
                // filters out past classes
                false
            }
        }.sortedBy { formatDateUtils.convertTimeToMinutes(it.classStartTime) }

        classList = filteredList
        notifyDataSetChanged()
    }

}