import com.example.gym_application.model.ClassModel
import com.google.firebase.database.*
import com.google.firebase.database.IgnoreExtraProperties

class FirebaseDatabaseHelper {

    private val database = FirebaseDatabase.getInstance().reference.child("classes")

    fun getClassesForDate(selectedDate: String, callback: (List<ClassModel>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val classList = mutableListOf<ClassModel>()
                for (classSnapshot in snapshot.children) {
                    val classData = classSnapshot.getValue(ClassModel::class.java)
                    classData?.let {
                        if (it.occurrences.contains(selectedDate)) { // Match with selected day
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
}
