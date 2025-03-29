package com.example.gym_application.controller


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
import com.example.gym_application.model.ClassWithScheduleModel
import com.example.gym_application.utils.ClassBookingUtils
import com.example.gym_application.utils.ValidationClassCreation.isClassUpcomingOrToday

class BookedClassesAdapter (private var classList: List<ClassWithScheduleModel>) :
    RecyclerView.Adapter<BookedClassesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val bookedClassTitle: TextView = view.findViewById(R.id.recyBookedClassTitle)
        val bookedClassDate: TextView = view.findViewById(R.id.recyBookedClassDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booked_classes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val bookedClass = classList[position]
        holder.bookedClassTitle.text = bookedClass.classTitle
        holder.bookedClassDate.text = bookedClass.classStartDate

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, ClassDetailViewActivity::class.java).apply {
                putExtra("scheduleId",bookedClass.scheduleId)
                putExtra("classId", bookedClass.classId)
                putExtra("classTitle", bookedClass.classTitle)
                putExtra("classStartDate", bookedClass.classStartDate)
                putExtra("classStartTime", bookedClass.classStartTime)
                putExtra("classEndTime", bookedClass.classEndTime)
                putExtra("classLocation", bookedClass.classLocation)
                putExtra("classAvailabilityFor", bookedClass.classAvailabilityFor)
                putExtra("classInstructor",bookedClass.classInstructor)
                putExtra("classCurrentBookings", bookedClass.classCurrentBookings)
                putExtra("classMaxCapacity", bookedClass.classMaxCapacity)
                putExtra("classDescription", bookedClass.classDescription)
            }
            holder.itemView.context.startActivity(intent)
        }


    }

    override fun getItemCount() = classList.size

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateData(newList: List<ClassWithScheduleModel>) {
        val filteredList = newList.filter { isClassUpcomingOrToday(it) }
            .sortedBy { convertTimeToMinutes(it.classStartTime) }

        classList = filteredList
        notifyDataSetChanged()
    }

    private fun convertTimeToMinutes(time: String): Int {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        return (hours * 60) + minutes
    }
}