package helper
import android.util.Log
import com.example.gym_application.model.FAQ
import com.google.firebase.database.*
class FirebaseFAQHelper {


    fun createFAQEntry(
        id : String,
        newFAQTemplate : FAQ,
        onSuccess : () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val faqRef = FirebaseDatabase.getInstance().getReference("faq")
        faqRef.child(id).setValue(newFAQTemplate)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure (exception) }
    }

    fun fetchAllFAQAsList ( callback : (List<FAQ>) -> Unit) {

        val faqRef = FirebaseDatabase.getInstance().getReference("faq")

        faqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val faqs = mutableListOf<FAQ>()
                for (planSnapshot in snapshot.children) {
                    val plan = planSnapshot.getValue(FAQ::class.java)
                    if (plan != null) {
                        faqs.add(plan)
                    }
                }
                callback(faqs)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data", error.toException())
            }
        })


    }



}