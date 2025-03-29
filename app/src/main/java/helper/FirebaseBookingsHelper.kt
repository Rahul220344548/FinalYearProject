package helper
import androidx.appcompat.view.ActionMode.Callback
import com.example.gym_application.model.UserDetails
import com.google.firebase.database.*
class FirebaseBookingsHelper {

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



}