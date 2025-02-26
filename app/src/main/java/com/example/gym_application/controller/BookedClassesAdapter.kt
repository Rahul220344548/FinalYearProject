package com.example.gym_application.controller

import GymClassesAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.model.ClassModel

class BookedClassesAdapter (private var classList: List<ClassModel>) :
    RecyclerView.Adapter<BookedClassesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val classTitle: TextView = view.findViewById(R.id.recyClassTitle)
        val classStartTime: TextView = view.findViewById(R.id.recyClassStartTime)
        val classLength : TextView = view.findViewById(R.id.recyClassLength)
        val classAvailabilityFor: TextView = view.findViewById(R.id.recyclassAvailabilityFor)
        val remainingBookings: TextView = view.findViewById(R.id.remainBookings)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_classes_plan , parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val bookedClass = classList[position]


    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}