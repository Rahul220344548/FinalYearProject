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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import com.example.gym_application.controller.ScheduleFirebaseHelper
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.model.ClassWithScheduleModel
import com.example.gym_application.utils.formatDateUtils
import com.example.gym_application.utils.formatDateUtils.getTodayDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var txtUserName: TextView

    private lateinit var txtMembershipType: TextView
    private lateinit var txtPlanExpiration: TextView
    private lateinit var txtMembershipStatus: TextView
    private lateinit var membershipCard: LinearLayout

    private lateinit var bookingsCard : ConstraintLayout
    private lateinit var txtUpcomingBookingTitle : TextView
    private lateinit var txtUpcomingClassDate : TextView
    private lateinit var txtUpcomingClassTimes : TextView
    private lateinit var txtUpcomingClassLocation : TextView
    private lateinit var txtnoUpcominbBooking : TextView

    private lateinit var rejoinMembershipButton: Button
    private lateinit var buyMembershipButton: Button


    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val firebaseHelper = UserFirebaseDatabaseHelper()
    private val scheduleFirebaseHelper = ScheduleFirebaseHelper()

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

        bookingsCard = view.findViewById(R.id.booked_class_card)
        txtUpcomingBookingTitle =  view.findViewById(R.id.txtUpcomingClassTitle)
        txtUpcomingClassDate = view.findViewById(R.id.txtUpcomingClassDate)
        txtUpcomingClassTimes = view.findViewById(R.id.txtUpcomingClassTimes)
        txtUpcomingClassLocation = view.findViewById(R.id.txtUpcomingClassLocation)
        txtnoUpcominbBooking = view.findViewById(R.id.txtnoUpcominbBookings)

        setUserName()
        checkMembershipStatus()
        displayUpcomingBookings()


        val btnReJoin = view.findViewById<AppCompatButton>(R.id.btnReJoin)
        val btnPurchaseMembership = view.findViewById<AppCompatButton>(R.id.btnPurchaseMembership)
        val btngoToBookClassPage = view.findViewById<Button>(R.id.buttonGoToClasses)
        btnReJoin.setOnClickListener {
            goToMembershipPage()
        }
        btnPurchaseMembership.setOnClickListener {
            goToMembershipPage()
        }

        btngoToBookClassPage.setOnClickListener {
            (activity as? HomeActivity)?.navigateToClassesTab()
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
                        val formattedExpirationDate = formatDateUtils.formatExpirationDate(membershipInfo.expirationDate)
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
    private fun displayUpcomingBookings() {
        val userId = auth.currentUser?.uid ?: return
        val todayDate = formatDateUtils.getTodayDate()

        firebaseHelper.fetchUserCurrentBookings(userId) { fetchedClassIds ->
            if (fetchedClassIds.isNullOrEmpty()) {
                txtUpcomingBookingTitle.text = "No upcoming bookings"
                return@fetchUserCurrentBookings
            }

            scheduleFirebaseHelper.fetchClassesForADate(todayDate) { allClassesForToday ->
                val upcomingBookings = getUpcomingUserBookingsForToday(allClassesForToday, fetchedClassIds)

                if (upcomingBookings.isNotEmpty()) {
                    displayBookingDetails(upcomingBookings.first())
                } else {
                    txtUpcomingBookingTitle.text = "No bookings for today"
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayBookingDetails(classModel: ClassWithScheduleModel) {
        bookingsCard.visibility = View.VISIBLE
        txtUpcomingBookingTitle.text = classModel.classTitle
        txtUpcomingClassDate.text = formatDateUtils.newFormatExpirationDate(classModel.classStartDate)
        txtUpcomingClassTimes.text = "${classModel.classStartTime} - ${classModel.classEndTime}"
        txtUpcomingClassLocation.text = classModel.classLocation
        txtnoUpcominbBooking.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUpcomingUserBookingsForToday(
        allClassesForToday: List<ClassWithScheduleModel>,
        bookedClassIds: List<String>
    ): List<ClassWithScheduleModel> {
        return allClassesForToday
            .filter { it.classId in bookedClassIds && !formatDateUtils.isClassOver(it) }
            .sortedBy {
                LocalTime.parse(it.classStartTime, DateTimeFormatter.ofPattern("HH:mm"))
            }
    }



}





