package com.example.gym_application.admin_view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.AdminClassEditorActivity
import com.example.gym_application.ClassDetailViewActivity
import com.example.gym_application.R
import com.example.gym_application.model.ClassModel


class AdminBookingListAdapter ( private  var classList : List<ClassModel>)  :
    RecyclerView.Adapter<AdminBookingListAdapter.ViewHolder>() {

    class ViewHolder ( view: View) : RecyclerView.ViewHolder(view) {

        val classTitle: TextView = view.findViewById(R.id.classListTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_class_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingListItem= classList[position]
        holder.classTitle.text = bookingListItem.classTitle

        holder.itemView.setOnClickListener {
            val intent =
                Intent(holder.itemView.context, AdminClassEditorActivity::class.java).apply {
                    putExtra("classId", bookingListItem.classId)
                    putExtra("classTitle", bookingListItem.classTitle)
                    putExtra("classStartDate", bookingListItem.classStartDate)
                    putExtra("classStartTime", bookingListItem.classStartTime)
                    putExtra("classEndTime", bookingListItem.classEndTime)
                    putExtra("classLocation", bookingListItem.classLocation)
                    putExtra("classAvailabilityFor", bookingListItem.classAvailabilityFor)
                    putExtra("classInstructor",bookingListItem.classInstructor)
                    putExtra("classCurrentBookings", bookingListItem.classCurrentBookings)
                    putExtra("classMaxCapacity", bookingListItem.classMaxCapacity)
                    putExtra("classDescription", bookingListItem.classDescription)

                }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = classList.size

    fun updateData( newList: List<ClassModel>) {
        classList = newList
        notifyDataSetChanged()
    }

}