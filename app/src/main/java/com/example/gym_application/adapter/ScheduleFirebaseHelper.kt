package com.example.gym_application.adapter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.gym_application.model.ClassTemplate
import com.example.gym_application.model.ClassWithScheduleModel
import com.example.gym_application.model.Schedule
import com.example.gym_application.newModel.NewSchedule
import com.example.gym_application.newModel.booking
import com.example.gym_application.utils.formatDateUtils
import com.google.firebase.database.*

class ScheduleFirebaseHelper {

    fun fetchClassTemplateByClassId(
        classId: String,
        onSuccess: (ClassTemplate) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("classTemplates").child(classId)
    }

    fun newCreateClassScheduleEntry(
        schedule: NewSchedule,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ){
        val databaseRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")
        val newScheduleId = databaseRef.push().key ?: return
        val scheduleWithId = schedule.copy(scheduleId = newScheduleId)
        databaseRef.child(newScheduleId).setValue(scheduleWithId)
            .addOnSuccessListener { onSuccess(newScheduleId) }
            .addOnFailureListener { exception -> onFailure(exception) }

    }

    fun createClassScheduleEntry(
        schedule: Schedule,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val databaseRef = FirebaseDatabase.getInstance().getReference("schedules")

        val newScheduleId = databaseRef.push().key ?: return

        val scheduleWithId = schedule.copy(scheduleId = newScheduleId)

        databaseRef.child(newScheduleId).setValue(scheduleWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateScheduleDetails(
        classId: String,
        updatedFields: Map<String, Any>,
        callback: (Boolean) -> Unit
    ) {
        val scheduleRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")

        scheduleRef.get().addOnSuccessListener { snapshot ->
            var matched = 0
            var completed = 0
            var anySuccess = false

            for (scheduleSnapshot in snapshot.children) {
                val data = scheduleSnapshot.value as? Map<*, *> ?: continue

                if (
                    data["classId"] == classId &&
                    data["status"] == "active"
                    ) {

                    val startDate = data["classStartDate"] as? String ?: continue
                    val endTime = data["classEndTime"] as? String ?: continue

                    if (!formatDateUtils.isClassOverForSchedules(startDate, endTime)) {
                        val scheduleId = scheduleSnapshot.key ?: continue
                        matched++

                        scheduleRef.child(scheduleId).updateChildren(updatedFields)
                            .addOnSuccessListener {
                                anySuccess = true
                                completed++
                                if (completed == matched) callback(anySuccess)
                            }
                            .addOnFailureListener {
                                completed++
                                if (completed == matched) callback(anySuccess)
                            }
                    }

                }
            }
            if (matched == 0) {
                callback(false)
            }
        }.addOnFailureListener {
            Log.e("FIREBASE", "Database read failed: ${it.message}")
            callback(false)
        }
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

    fun fetchClassesForADate(
        selectedDate: String, callback: (List<ClassWithScheduleModel>) -> Unit
    ) {

        val database = FirebaseDatabase.getInstance().reference
        val scheduleRef = database.child("schedules")
        val classRef = database.child("classes")

//        fetchSchedulesForDateLive(scheduleRef, selectedDate) { schedules ->
//            fetchClassTemplates(classRef) { templates ->
//                val mergedClasses = mergeSchedulesWithTemplates(schedules, templates)
//                callback(mergedClasses)
//            }
//        }

    }

    private var scheduleListener: ValueEventListener? = null
    private lateinit var savedScheduleRef: DatabaseReference

    fun fetchSchedulesForDateLive(
        scheduleRef: DatabaseReference,
        selectedDate: String,
        callback: (List<NewSchedule>) -> Unit
    ) {
        removeScheduleListener()

        savedScheduleRef = scheduleRef

        scheduleListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val schedules = snapshot.children
                    .mapNotNull { it.getValue(NewSchedule::class.java) }
                    .filter {
                        it.classStartDate.contains(selectedDate) && it.status == "active"
                    }
                callback(schedules)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        }

        scheduleRef.addValueEventListener(scheduleListener!!)
    }


    private fun fetchClassTemplates(
        templateRef: DatabaseReference,
        callback: (Map<String, ClassTemplate>) -> Unit
    ) {
        templateRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val templateMap = snapshot.children.mapNotNull {
                    it.getValue(ClassTemplate::class.java)
                }.associateBy { it.classId }

                callback(templateMap)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching class templates: ${error.message}")
                callback(emptyMap())
            }
        })
    }


    private fun mergeSchedulesWithTemplates(
        schedules: List<Schedule>,
        templates: Map<String, ClassTemplate>
    ): List<ClassWithScheduleModel> {
        return schedules.mapNotNull { schedule ->
            val template = templates[schedule.classId]
            template?.let {
                ClassWithScheduleModel(
                    scheduleId = schedule.scheduleId,
                    classId = schedule.classId,
                    classTitle = it.classTitle,
                    classDescription = it.classDescription,
                    classColor = it.classColor,
                    classMaxCapacity = it.classMaxCapacity,
                    classAvailabilityFor = it.classAvailabilityFor,
                    classLocation = schedule.classLocation,
                    classInstructor = schedule.classInstructor,
                    classStartTime = schedule.classStartTime,
                    classEndTime = schedule.classEndTime,
                    classStartDate = schedule.classStartDate,
                    classCurrentBookings = schedule.classCurrentBookings,
                    status = schedule.status
                )
            }
        }
    }

    fun incrementClassCurrentBookingInSchedules(
        scheduleId: String,
        currentBookingCount: Map<String, Any>,
        callback: (Boolean) -> Unit
    ) {

        val schedulesRef = FirebaseDatabase.getInstance().getReference("schedules")
            .child(scheduleId)
        schedulesRef.updateChildren(currentBookingCount)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun newIncrementClassCurrentBookingInSchedules(
        scheduleId: String,
        currentBookingCount: Map<String, Any>,
        callback: (Boolean) -> Unit
    ) {

        val schedulesRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")
            .child(scheduleId)
        schedulesRef.updateChildren(currentBookingCount)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun removeScheduleListener() {
        scheduleListener?.let {
            savedScheduleRef.removeEventListener(it)
            scheduleListener = null
        }
    }

    fun addUserBookingToSchedules(
        scheduleId: String,
        userId: String,
        booking: booking,
        callback: (Boolean) -> Unit
    ) {

        val scheduleBookingsRef = FirebaseDatabase.getInstance()
            .getReference("schedules")
            .child(scheduleId)
            .child("bookings")
            .child(userId)

        scheduleBookingsRef.setValue(booking)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    fun newAddUserBookingToSchedules(
        scheduleId: String,
        userId: String,
        booking: booking,
        callback: (Boolean) -> Unit
    ) {

        val scheduleBookingsRef = FirebaseDatabase.getInstance()
            .getReference("schedulesInfo")
            .child(scheduleId)
            .child("bookings")
            .child(userId)

        scheduleBookingsRef.setValue(booking)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    fun deleteUserBookingsFromSchedule(
        scheduleId: String,
        userId: String,
        callback: (Boolean) -> Unit
    ) {

        val scheduleBookingsRef = FirebaseDatabase.getInstance()
            .getReference("schedulesInfo")
            .child(scheduleId)
            .child("bookings")
            .child(userId)

        scheduleBookingsRef.removeValue().addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }

    }

    private val scheduleListeners = mutableMapOf<String, ValueEventListener>()

    fun listenToBookedSchedulesFullDetail(
        scheduleId: String,
        onUpdate: (schedule: NewSchedule?) -> Unit
    ) {
        val scheduleRef = FirebaseDatabase.getInstance().reference
            .child("schedulesInfo")
            .child(scheduleId)


        if (scheduleListeners.containsKey(scheduleId)) return

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scheduleDetails = snapshot.getValue(NewSchedule::class.java)
                if (scheduleDetails != null) {
                    onUpdate(scheduleDetails)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LiveSchedule", "Error: ${error.message}")
                onUpdate(null)
            }
        }

        scheduleRef.addValueEventListener(listener)
        scheduleListeners[scheduleId] = listener
    }


    fun removeAllScheduleListeners() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")
        for ((scheduleId, listener) in scheduleListeners) {
            databaseRef.child(scheduleId).removeEventListener(listener)
        }
        scheduleListeners.clear()
    }

    fun listenForScheduleUpdates(
        onDataChanged : (List<NewSchedule>) -> Unit){

        val scheduleRef = FirebaseDatabase.getInstance().reference
            .child("schedulesInfo")

        scheduleRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val scheduleList = mutableListOf<NewSchedule>()
                for (scheduleSnapshot in snapshot.children) {
                    val scheduleData =  scheduleSnapshot.getValue(NewSchedule::class.java)
                    scheduleData?.let { scheduleList.add(it) }
                }
                onDataChanged(scheduleList)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error Fetching Schedules: ${error.message}")
            }

        })

    }

    fun reactiveSchedule(
        scheduleId: String,
        onComplete : (Boolean) -> Unit
    ) {

        val scheduleRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")
            .child(scheduleId)

        scheduleRef.child("status").setValue("active")
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setExpiredSchedulesToInactive(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val schedulesRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")

        schedulesRef.get().addOnSuccessListener { snapshot ->
            for (scheduleSnapshot in snapshot.children) {
                val data = scheduleSnapshot.getValue(NewSchedule::class.java) ?: continue
                val startDate = data.classStartDate ?: continue
                val endTime = data.classEndTime ?: continue
                val scheduleId = data.scheduleId ?: continue
                val isOver = formatDateUtils.isClassOverForSchedules(startDate, endTime)
                if (isOver && data.status != "inactive") {
                    schedulesRef.child(scheduleId).child("status").setValue("inactive")
                }
            }
            onSuccess()

        }.addOnFailureListener { exception ->
            onFailure(exception)
        }

    }






}

