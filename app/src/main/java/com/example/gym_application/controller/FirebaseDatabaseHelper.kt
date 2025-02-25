import android.widget.Toast
import com.example.gym_application.model.ClassModel
import com.example.gym_application.model.UserClassBooking
import com.google.firebase.database.*

class FirebaseDatabaseHelper {

    private val database = FirebaseDatabase.getInstance().reference.child("classes")

    fun getClassesForDate(selectedDate: String, callback: (List<ClassModel>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val classList = mutableListOf<ClassModel>()
                for (classSnapshot in snapshot.children) {
                    val classData = classSnapshot.getValue(ClassModel::class.java)
                    classData?.let {
                        if (it.classStartDate.contains(selectedDate)) { // Match with selected day
                            classList.add(it)
                        }
                    }
                }
                callback(classList) // Return the filtered class list
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching classes: ${error.message}")
                callback(emptyList()) // Return an empty list in case of failure
            }
        })
    }



    fun hasUserAlreadyBookedThisClass(userId: String, classId: String, callback: (Boolean) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            .child(userId)
            .child("bookings")
            .child("current")

        databaseReference.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                var isBooked = false

                for (dataSnapshot in snapshot.children) {
                    val userClassBooking = dataSnapshot.getValue(UserClassBooking::class.java)
                    if (userClassBooking?.classId == classId) {
                        isBooked = true
                        break
                    }
                }
                callback(isBooked)
            } else {
                callback(false)
            }
        }.addOnFailureListener {
            println("Error fetching data: ${it.message}")
            callback(false)
        }
    }

    fun getUserMembershipStatus(userId:String,callback: (String?) -> Unit ) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            .child(userId)
            .child("membershipDetails")

        databaseReference.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val membershipStatus = snapshot.child("membershipStatus").value.toString()
                    callback(membershipStatus)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null)
            }
    }

    fun getUserGender(userId: String ,callback: (String?) -> Unit ) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            .child(userId)

        databaseReference.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val gender = snapshot.child("gender").value.toString()
                    callback(gender)
                } else {
                    callback(null)
                }
            }
            .addOnSuccessListener { exception ->
                callback(null)
            }
    }
}
