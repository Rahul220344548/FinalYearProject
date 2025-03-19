package com.example.gym_application

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

    private val firebaseHelper = UserFirebaseDatabaseHelper()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")


        txtUserName = view.findViewById(R.id.txt_userName)

        txtMembershipType = view.findViewById(R.id.MembershipTypeTitle)
        txtPlanExpiration =  view.findViewById(R.id.ExpiresEndDate)
        membershipCard = view.findViewById(R.id.membershipStatusCard)
        buyMembershipButton = view.findViewById(R.id.btnPurchaseMembership)
        rejoinMembershipButton = view.findViewById(R.id.btnReJoin)


        setUserName()
        checkMembershipStatus()


        val btnReJoin = view.findViewById<AppCompatButton>(R.id.btnReJoin)
        val btnPurchaseMembership = view.findViewById<AppCompatButton>(R.id.btnPurchaseMembership)

        btnReJoin.setOnClickListener {
            goToMembershipPage()
        }
        btnPurchaseMembership.setOnClickListener {
            goToMembershipPage()
        }
        return view
    }

    private fun goToMembershipPage(){
        val intent = Intent(requireContext(), MembershipActivity::class.java)
        startActivity(intent)
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

    @RequiresApi(Build.VERSION_CODES.O)

    private fun checkMembershipStatus() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firebaseHelper.fetchUserMembershipInfo(userId) { membershipInfo ->
                if (membershipInfo != null) {
                    if (membershipInfo.status == "active") {
                        txtMembershipType.text = "${membershipInfo.title} - ${membershipInfo.duration} Plan"
                        val formattedExpirationDate = formatExpirationDate(membershipInfo.expirationDate)
                        txtPlanExpiration.text = "Expires - $formattedExpirationDate"
                        membershipCard.visibility = View.VISIBLE
                    } else {
                        rejoinMembershipButton.visibility = View.VISIBLE
                    }
                } else {
                    buyMembershipButton.visibility = View.VISIBLE
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatExpirationDate(expirationDate : String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.US)
        val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)

        val date = LocalDate.parse(expirationDate, inputFormatter)
        return date.format(outputFormatter)

    }
}





