package com.example.gym_application.controller


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.ClassDetailViewActivity
import com.example.gym_application.R
import com.example.gym_application.model.ClassModel
import com.example.gym_application.utils.ClassBookingUtils

class BookedClassesAdapter (private var classList: List<ClassModel>) :
    RecyclerView.Adapter<BookedClassesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val bookedClassTitle: TextView = view.findViewById(R.id.recyBookedClassTitle)
        val bookedClassStartTime: TextView = view.findViewById(R.id.recyBookedStartTime)
        val bookedClassLength : TextView = view.findViewById(R.id.recyBookedLength)
        val bookedClassStartDate: TextView = view.findViewById(R.id.recyBookedStartDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booked_classes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val bookedClass = classList[position]
        holder.bookedClassTitle.text = bookedClass.classTitle
        holder.bookedClassStartTime.text = "${bookedClass.classStartTime} - ${bookedClass.classEndTime}"
        
        val startMinutes = ClassBookingUtils.convertTimeToMinutes(bookedClass.classStartTime)
        val endMinutes =  ClassBookingUtils.convertTimeToMinutes(bookedClass.classEndTime)
        val durationMinutes = endMinutes - startMinutes
        
        holder.bookedClassLength.text = "$durationMinutes"
        
        holder.bookedClassStartDate.text = "Start Date: ${bookedClass.classStartDate}"
        
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, ClassDetailViewActivity::class.java).apply {
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

    fun updateData( newList : List<ClassModel>) {
        classList = newList
        notifyDataSetChanged()
    }

}