package helper
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



}