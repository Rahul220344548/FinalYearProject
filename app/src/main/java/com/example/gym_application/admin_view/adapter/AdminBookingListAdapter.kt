package com.example.gym_application.admin_view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminClassListAdapter.ViewHolder

import com.example.gym_application.model.UserDetails
import com.example.gym_application.utils.OnDeleteClickListener

class AdminBookingListAdapter (
    private  var bookingList : List<UserDetails>,
//    private val deleteClickListener: OnDeleteClickListener
)  :
    RecyclerView.Adapter<AdminBookingListAdapter.ViewHolder>() {

    class ViewHolder ( view: View) : RecyclerView.ViewHolder(view) {

        val bookingUserName: TextView = view.findViewById(R.id.bookedUserFullName)
        val bookingUserDOB : TextView = view.findViewById(R.id.bookedUserDOB)
//        val deleteIcon: View = view.findViewById(R.id.deleteUserBooking)

    }

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_schedule_bookings, parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val bookingListItem = bookingList[position]
        holder.bookingUserName.text = "${bookingListItem.firstName} ${bookingListItem.lastName}"
        holder.bookingUserDOB.text = bookingListItem.dateOfBirth

//        holder.deleteIcon.setOnClickListener {
//            deleteClickListener.onDeleteClick(bookingListItem)
//        }



    }



    override fun getItemCount(): Int = bookingList.size


    fun updateData( newBookings : List<UserDetails>) {
        bookingList = newBookings
        notifyDataSetChanged()
    }

}