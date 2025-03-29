package com.example.gym_application.admin_view.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.model.ClassTemplate

class AdminBookingListAdapter (private  var bookingList : List<ClassTemplate>)  :
    RecyclerView.Adapter<AdminClassListAdapter.ViewHolder>() {

    class ViewHolder ( view: View) : RecyclerView.ViewHolder(view) {

        val classTitle: TextView = view.findViewById(R.id.classListTitle)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminClassListAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: AdminClassListAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


}