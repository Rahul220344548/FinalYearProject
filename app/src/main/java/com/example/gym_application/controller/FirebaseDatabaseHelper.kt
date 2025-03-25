import com.example.gym_application.model.ClassWithScheduleModel
import com.example.gym_application.model.ClassTemplate
import com.example.gym_application.model.UserClassBooking
import com.google.firebase.database.*

class FirebaseDatabaseHelper {

    fun fetchClassName(callback: (Map<String, String>) -> Unit) {
        val classDatabase = FirebaseDatabase.getInstance().getReference("classes")
        val classMap = mutableMapOf<String, String>()

        classDatabase.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (classSnapshot in snapshot.children) {
                    val classId = classSnapshot.child("classId").getValue(String::class.java) ?: ""
                    val classTitle = classSnapshot.child("classTitle").getValue(String::class.java) ?: ""
                    if (classId.isNotEmpty() && classTitle.isNotEmpty()) {
                        classMap[classTitle] = classId
                    }
                }
                callback(classMap)
            }else {
                callback(emptyMap())
            }
        }.addOnFailureListener {
            callback(emptyMap())
        }

    }

    fun getClassesForDate(selectedDate: String, callback: (List<ClassWithScheduleModel>) -> Unit) {
        val classDatabase = FirebaseDatabase.getInstance().reference.child("classes")
        classDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val classList = mutableListOf<ClassWithScheduleModel>()
                for (classSnapshot in snapshot.children) {
                    val classData = classSnapshot.getValue(ClassWithScheduleModel::class.java)
                    classData?.let {
                        if (it.classStartDate.contains(selectedDate)) {
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

    fun createClassTemplate( classId: String, classTemplate: ClassTemplate, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

        val databaseRef = FirebaseDatabase.getInstance().getReference("classes")

        databaseRef.child(classId).setValue(classTemplate)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure (exception) }

    }

    fun hasUserAlreadyBookedThisClass(userId: String, classId: String, callback: (Boolean) -> Unit) {
        val userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            .child("bookings")
            .child("current")

        userDatabase.get().addOnSuccessListener { snapshot ->
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
        val userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("membershipDetails")

        userDatabase.get()
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
        val userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId)

        userDatabase.get()
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

    fun deleteUserCurrentBookingById(userId: String, classId: String,callback: (Boolean) -> Unit) {
        val userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(userId).child("bookings").child("current")

        userDatabase.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                var bookingDeleted = false

                for (dataSnapshot in snapshot.children) {
                    val userClassBooking = dataSnapshot.getValue(UserClassBooking::class.java)
                    if (userClassBooking?.classId == classId) {
                        // Delete the specific booking
                        dataSnapshot.ref.removeValue().addOnSuccessListener {
                            bookingDeleted = true
                            callback(true) // Successfully deleted
                        }.addOnFailureListener {
                            println("Error deleting booking: ${it.message}")
                            callback(false)
                        }
                        break
                    }
                }

                if (!bookingDeleted) {
                    callback(false) // Booking not found
                }
            } else {
                callback(false) // No bookings exist
            }
        }.addOnFailureListener {
            println("Error fetching data: ${it.message}")
            callback(false)
        }
    }

    fun decrementClassCurrentBookings(classId: String, currentBookingCount: Map<String, Int>, callback: (Boolean) -> Unit) {
        val classDatabase = FirebaseDatabase.getInstance().reference.child("classes").child(classId)

        // Convert Int values to Any? to match Firebase expectations
        val updateData = currentBookingCount.mapValues { it.value as Any? } // ✅ Convert values properly

        classDatabase.updateChildren(updateData)
            .addOnSuccessListener {
                println("Successfully decremented bookings for class $classId")
                callback(true)
            }
            .addOnFailureListener { error ->
                println("Error decrementing bookings: ${error.message}") // ✅ Debugging output
                callback(false)
            }
    }

    fun getClassesFullDetails(classId: String , callback: (ClassWithScheduleModel?) -> Unit) {

        val classDatabase = FirebaseDatabase.getInstance().reference.child("classes")
            .child(classId)

        classDatabase.get().addOnSuccessListener { snapshot ->

            if (snapshot.exists()) {
                val classDetails = snapshot.getValue(ClassWithScheduleModel::class.java)
                callback(classDetails)
            }else {
                callback(null)
            }

        }.addOnFailureListener {
            callback(null)
        }



    }

    fun listenForClassUpdates(onDataChanged: (List<ClassTemplate>) -> Unit) {
        val classDatabase = FirebaseDatabase.getInstance().reference.child("classes")

        classDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val classList = mutableListOf<ClassTemplate>()
                for (classSnapshot in snapshot.children) {
                    val classData = classSnapshot.getValue(ClassTemplate::class.java)
                    classData?.let { classList.add(it) }
                }
                onDataChanged(classList)
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error Fetching Classes: ${error.message}")
            }
        })

    }



    fun deleteClass (classId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

        val classDatabase = FirebaseDatabase.getInstance().reference.child("classes")
            .child(classId)

        classDatabase.removeValue()

            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }

    }

    fun updateClassDetails(classId: String, classUpdate: Map<String, Any>, callback: (Boolean) -> Unit) {
        val classDatabase = FirebaseDatabase.getInstance().reference.child("classes")
            .child(classId)

        classDatabase.updateChildren(classUpdate)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }

    }

}
