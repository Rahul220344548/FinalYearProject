package com.example.gym_application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.w3c.dom.Text

class HomeFragment : Fragment() {

    private lateinit var txtUserName: TextView

    private lateinit var txtMembershipType: TextView
    private lateinit var txtPlanExpiration: TextView
    private lateinit var txtMembershipStatus: TextView
    private lateinit var membershipCard: LinearLayout

    private lateinit var rejoinMembershipButton: Button
    private lateinit var buyMembershipButton: Button


    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")


        txtUserName = view.findViewById(R.id.txt_userName)

        txtMembershipType = view.findViewById(R.id.MembershipTypeTitle)

        membershipCard = view.findViewById(R.id.membershipStatusCard)
        buyMembershipButton = view.findViewById(R.id.btnPurchaseMembership)
        rejoinMembershipButton = view.findViewById(R.id.btnReJoin)


        setUserName()
        checkMembershipStatus()
        return view
    }

    private fun setUserName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.child(userId).child("firstName")

            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val firstName = snapshot.value.toString()
                    txtUserName.text = firstName
                } else {
                    txtUserName.text = "User Not Found"
                }
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            txtUserName.text = "Not Logged In"
        }
    }

    private fun checkMembershipStatus() {

        val userId = auth.currentUser?.uid

        if (userId != null) {
            val membershipRef = database.child(userId).child("membershipDetails")

            membershipRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {

                    val membershipTitle = snapshot.child("membershipTitle").value?.toString() ?: "No Plan"
                    val membershipDuration = snapshot.child("membershipDuration").value?.toString() ?: "Unknown"
                    val membershipStatus = snapshot.child("membershipStatus").value?.toString()?.lowercase() ?: "inactive"

                    if (membershipStatus == "active") {
                        txtMembershipType.text = "$membershipTitle - $membershipDuration Plan"
                        membershipCard.visibility = View.VISIBLE

                    } else {
                        membershipCard.visibility = View.GONE
                        rejoinMembershipButton.visibility = View.VISIBLE
                    }
                } else {
                    membershipCard.visibility = View.GONE
                    buyMembershipButton.visibility = View.VISIBLE
                }
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }

        }

    }
}





