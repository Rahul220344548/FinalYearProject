package com.example.gym_application.admin_view.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.AdminScheduleInfo
import com.example.gym_application.newModel.NewSchedule

class AdminAllScheduleListAdapter (private var scheduleList : List<NewSchedule>) :
    RecyclerView.Adapter<AdminAllScheduleListAdapter.ViewHolder>(){

    class ViewHolder (view : View) : RecyclerView.ViewHolder(view) {
        val scheduleTitle : TextView = view.findViewById(R.id.recyAllSchedulesTitle)
        val scheduleDate : TextView = view.findViewById(R.id.recyAllSchedulesStartTime)
        val scheduleDuration : TextView = view.findViewById(R.id.recyAllSchedulesLength)
        val scheduleStatus : TextView = view.findViewById(R.id.recyAllSchedulesStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_all_schedules, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedules = scheduleList[position]
        holder.scheduleTitle.text = schedules.classTitle
        holder.scheduleDate.text = schedules.classStartDate
        holder.scheduleDuration.text =  "${schedules.classStartTime} - ${schedules.classEndTime}"

        holder.scheduleStatus.text = schedules.status
        setStatusColor(holder.scheduleStatus, schedules.status)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AdminScheduleInfo::class.java).apply {
                putExtra("scheduleId",schedules.scheduleId)
                putExtra("classId", schedules.classId)
                putExtra("classTitle", schedules.classTitle)
                putExtra("classStartDate", schedules.classStartDate)
                putExtra("classStartTime", schedules.classStartTime)
                putExtra("classEndTime", schedules.classEndTime)
                putExtra("classLocation", schedules.classLocation)
                putExtra("classAvailabilityFor", schedules.classAvailabilityFor)
                putExtra("classInstructor",schedules.classInstructor)
                putExtra("classCurrentBookings", schedules.classCurrentBookings)
                putExtra("classMaxCapacity", schedules.classMaxCapacity)
                putExtra("classDescription", schedules.classDescription)
                putExtra("status", schedules.status)

            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = scheduleList.size

    fun updateData( newList: List<NewSchedule>) {
        scheduleList = newList
        notifyDataSetChanged()
    }

    private fun setStatusColor(textView: TextView, status: String) {
        val color = if (status.equals("active", ignoreCase = true)) {
            Color.parseColor("#4CAF50")
        } else {
            Color.parseColor("#F44336")
        }
        textView.setTextColor(color)
    }

}