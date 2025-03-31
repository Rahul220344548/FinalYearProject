package helper
import com.example.gym_application.model.UserDetails
import com.example.gym_application.newModel.NewSchedule
import com.google.firebase.database.*
class FirebaseClassesHelper {



    fun getUserIdsForSchedule(
        scheduleId: String,
        callback: (List<String>) -> Unit
    ) {
        val scheduleRef = FirebaseDatabase.getInstance()
            .getReference("schedules")
            .child(scheduleId)
            .child("bookings")

        scheduleRef.get().addOnSuccessListener { snapshot ->
            val userIds = snapshot.children.mapNotNull { it.key }
            callback(userIds)
        }.addOnFailureListener {
            println("Error fetching userIds from schedule: ${it.message}")
            callback(emptyList())
        }
    }

    fun getUserDetailsById(
        userId: String,
        callback: (UserDetails?) -> Unit
    ) {
        val userRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)

        userRef.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(UserDetails::class.java)
            callback(user)
        }.addOnFailureListener {
            callback(null)
        }
    }


    fun fetchedSchedulesForADateLive(
        selectedDate: String,
        callback: (List<NewSchedule>) -> Unit
    ) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")

        databaseRef.orderByChild("classStartDate").equalTo(selectedDate)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val scheduleList = mutableListOf<NewSchedule>()
                    for (scheduleSnapshot in snapshot.children) {
                        val schedule = scheduleSnapshot.getValue(NewSchedule::class.java)
                        if (schedule != null && schedule.status == "active") {
                            scheduleList.add(schedule)
                        }
                    }
                    callback(scheduleList)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    fun softDeleteSchedule(
        scheduleId: String,
        onComplete : (Boolean) -> Unit
    ) {

        val scheduleRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")
            .child(scheduleId)

        scheduleRef.child("status").setValue("inactive")
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }



}