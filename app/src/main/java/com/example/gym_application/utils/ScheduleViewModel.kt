// ScheduleViewModel.kt
package com.example.gym_application.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gym_application.newModel.NewSchedule
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ScheduleViewModel : ViewModel() {
    private var listener: ValueEventListener? = null
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("schedulesInfo")

    fun attachListenerForDate(selectedDate: String, callback: (List<NewSchedule>, Boolean) -> Unit) {
        listener = databaseRef.orderByChild("classStartDate").equalTo(selectedDate)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val schedules = snapshot.children.mapNotNull { it.getValue(NewSchedule::class.java) }
                        .filter { it.status == "active" }
                        .sortedBy { it.classStartTime }
                    callback(schedules, false)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList(), true)
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        listener?.let { databaseRef.removeEventListener(it) }
    }
}
