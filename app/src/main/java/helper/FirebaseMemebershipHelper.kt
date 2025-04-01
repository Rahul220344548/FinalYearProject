package helper

import android.util.Log
import android.widget.Toast
import com.example.gym_application.model.MembershipPlans
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.callbackFlow

class FirebaseMemebershipHelper {

    fun fetchAllMembershipPlansAsList( callback: (List<MembershipPlans>) -> Unit) {

        val database = FirebaseDatabase.getInstance().getReference("membershipPlan")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plans = mutableListOf<MembershipPlans>()
                for (planSnapshot in snapshot.children) {
                    val plan = planSnapshot.getValue(MembershipPlans::class.java)
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

}