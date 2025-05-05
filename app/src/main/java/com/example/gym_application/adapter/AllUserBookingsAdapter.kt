package com.example.gym_application.adapter

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
import com.example.gym_application.newModel.NewSchedule
import com.example.gym_application.utils.formatDateUtils
import java.time.LocalDateTime

class AllUserBookingsAdapter (private var classList: List<NewSchedule>) :
    RecyclerView.Adapter<AllUserBookingsAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val bookedClassTitle: TextView = view.findViewById(R.id.recyClassTitle)
        val bookedClassDate: TextView = view.findViewById(R.id.recyClassStartTime)
        val bookedClassDuration: TextView = view.findViewById(R.id.recyClassLength)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scheduled_classes, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val classes = classList[position]
        holder.bookedClassTitle.text = classes.classTitle
        holder.bookedClassDate.text = classes.classStartDate
        holder.bookedClassDuration.text = "${classes.classStartTime} - ${classes.classEndTime}"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ClassDetailViewActivity::class.java).apply {
                putExtra("scheduleId",classes.scheduleId)
                putExtra("classId", classes.classId)
                putExtra("classTitle", classes.classTitle)
                putExtra("classStartDate", classes.classStartDate)
                putExtra("classStartTime", classes.classStartTime)
                putExtra("classEndTime", classes.classEndTime)
                putExtra("classLocation", classes.classLocation)
                putExtra("classAvailabilityFor", classes.classAvailabilityFor)
                putExtra("classInstructor",classes.classInstructor)
                putExtra("classCurrentBookings", classes.classCurrentBookings)
                putExtra("classMaxCapacity", classes.classMaxCapacity)
                putExtra("classDescription", classes.classDescription)
                putExtra("status", classes.status)
            }
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount() = classList.size

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateData(newList: List<NewSchedule>) {
        val now = LocalDateTime.now()

        val (upcoming, past) = newList.partition { schedule ->
            val dateTime = formatDateUtils.parseDateTime(schedule.classStartDate, schedule.classStartTime)
            dateTime != null && dateTime.isAfter(now)
        }

        val sortedUpcoming = upcoming.sortedBy { schedule ->
            formatDateUtils.parseDateTime(schedule.classStartDate, schedule.classStartTime)
        }

        val sortedPast = past.sortedByDescending { schedule ->
            formatDateUtils.parseDateTime(schedule.classStartDate, schedule.classStartTime)
        }

        classList = sortedUpcoming + sortedPast
        notifyDataSetChanged()
    }





}