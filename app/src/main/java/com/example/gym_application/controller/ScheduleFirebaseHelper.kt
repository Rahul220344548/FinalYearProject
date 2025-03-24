package com.example.gym_application.controller
import com.example.gym_application.model.Schedule
import com.google.firebase.database.*
import kotlin.time.measureTime

class ScheduleFirebaseHelper {

    fun createClassScheduleEntry(schedule: Schedule, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

        val databaseRef = FirebaseDatabase.getInstance().getReference("schedules")

        val newScheduleId = databaseRef.push().key ?: return

        val scheduleWithId = schedule.copy(scheduleId = newScheduleId)

        databaseRef.child(newScheduleId).setValue(scheduleWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure (exception) }

    }

    fun checkForDuplicateSchedules(
        classId: String,
        classStartDate: String,
        classStartTime: String,
        onResult: (Boolean) -> Unit
    ) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("schedules")

        databaseRef.get().addOnSuccessListener { snapshot ->
            var isDuplicate = false

            for (scheduleSnapshot in snapshot.children) {
                val schedule = scheduleSnapshot.getValue(Schedule::class.java)
                if (schedule != null &&
                    schedule.classId == classId &&
                    schedule.classStartDate == classStartDate &&
                    schedule.classStartTime == classStartTime
                ) {
                    isDuplicate = true
                    break
                }
            }
            onResult(isDuplicate)
        }.addOnFailureListener {
            onResult(false)
        }

    }


}