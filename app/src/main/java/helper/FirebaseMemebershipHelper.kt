package helper

import android.util.Log
import android.widget.Toast
import com.example.gym_application.model.MembershipPlans
import com.example.gym_application.newModel.NewMembershipPlan
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.callbackFlow

class FirebaseMemebershipHelper {

    fun fetchAllMembershipPlansAsList( callback: (List<NewMembershipPlan>) -> Unit) {

        val database = FirebaseDatabase.getInstance().getReference("memberships")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plans = mutableListOf<NewMembershipPlan>()
                for (planSnapshot in snapshot.children) {
                    val plan = planSnapshot.getValue(NewMembershipPlan::class.java)
                    if (plan != null) {
                        plans.add(plan)
                    }
                }
                callback(plans)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data", error.toException())
            }
        })

    }

    fun createMembershipEntry(
        membershipId : String,
        newMembershipPlan: NewMembershipPlan,
        onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

        val databaseRef = FirebaseDatabase.getInstance().getReference("memberships")

        databaseRef.child(membershipId).setValue(newMembershipPlan)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure (exception) }

    }

    fun updateMembershipInfo(
        planId : String,
        planUpdate: Map<String, Any>,
        callback: (Boolean) -> Unit
    ) {
        val membershipRef = FirebaseDatabase.getInstance().getReference("memberships")
            .child(planId)

        membershipRef.updateChildren(planUpdate)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }

    }

    fun deleteMembershipFromDatabase(
        planId : String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val membershipRef = FirebaseDatabase.getInstance().reference.child("memberships")
            .child(planId)

        membershipRef.removeValue()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }

    }


}