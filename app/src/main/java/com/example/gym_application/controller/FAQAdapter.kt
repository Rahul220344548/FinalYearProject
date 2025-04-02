package com.example.gym_application.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminBookingListAdapter.ViewHolder
import com.example.gym_application.model.FAQ
import kotlinx.coroutines.NonDisposableHandle.parent


class FAQAdapter (
    private var questionLists : List<FAQ>
):
    RecyclerView.Adapter<FAQAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_schedule_bookings, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = questionLists[position]

    }
    override fun getItemCount(): Int = questionLists.size

    fun updateData() {

    }

}