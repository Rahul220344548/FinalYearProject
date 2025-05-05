package com.example.gym_application

import FirebaseDatabaseHelper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_application.adapter.UserFirebaseDatabaseHelper
import com.example.gym_application.newModel.booking
import com.example.gym_application.utils.scheduleFirebaseHelper
import com.example.gym_application.utils.utilsSetUpBookAndCancelButton
import com.example.gym_application.utils.utilsSetUpClassAvailableFor
import com.example.gym_application.utils.utilsSetUpClassDescription
import com.example.gym_application.utils.utilsSetUpClassDuration
import com.example.gym_application.utils.utilsSetUpClassInstructor
import com.example.gym_application.utils.utilsSetUpClassLocation
import com.example.gym_application.utils.utilsSetUpClassStartDate
import com.example.gym_application.utils.utilsSetUpClassStartTime
import com.example.gym_application.utils.utilsSetUpClassStatus
import com.example.gym_application.utils.utilsSetUpClassTitle
import com.example.gym_application.utils.utilsUpdateClassCurrentBookings
import com.example.gym_application.utils.utilsValidateUserMembershipAndGender
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



@RequiresApi(Build.VERSION_CODES.O)
class ClassDetailViewActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var classRef: DatabaseReference

    private val userId = getCurrentUserId().toString()
    private val firebaseHelper = FirebaseDatabaseHelper()
    private val userFirebaseHelper = UserFirebaseDatabaseHelper()
    private lateinit var classId: String
    private lateinit var scheduleId : String
    private var currBookings: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_class_detail_view)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
        scheduleId = intent.getStringExtra("scheduleId")?:""
        classId = intent.getStringExtra("classId")?:""
        currBookings = intent.getIntExtra("classCurrentBookings",0)

        database = FirebaseDatabase.getInstance()
        classRef = database.getReference("schedules")

        setUpScheduleInfo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpScheduleInfo() {
        val inClassTitle = intent.getStringExtra("classTitle")
        val inClassStartDate = intent.getStringExtra("classStartDate") ?: "01/01/2000"
        val inClassStartTime = intent.getStringExtra("classStartTime") ?: "00:00"
        val inClassEndTime = intent.getStringExtra("classEndTime") ?: "00:00"
        val classAvailabilityFor = intent.getStringExtra("classAvailabilityFor") ?: ""
        val maxCapacity = intent.getIntExtra("classMaxCapacity",0)
        val currBookings = intent.getIntExtra("classCurrentBookings",0)
        val inClassLocation = intent.getStringExtra("classLocation") ?: ""
        val inClassInstructor = intent.getStringExtra("classInstructor") ?: ""
        val inClassDescription = intent.getStringExtra("classDescription") ?: ""
        val inClassStatus =  intent.getStringExtra("status") ?: ""

        utilsSetUpClassTitle(this, inClassTitle)
        utilsSetUpClassStartDate(this,inClassStartDate)
        utilsSetUpClassStartTime(this,inClassStartTime,inClassEndTime)
        utilsSetUpClassDuration(this,inClassStartTime,inClassEndTime)
        utilsSetUpClassAvailableFor(this,classAvailabilityFor)
        utilsSetUpClassStatus(this,maxCapacity,currBookings,inClassStatus)
        utilsSetUpClassLocation(this,inClassLocation)
        utilsSetUpClassInstructor(this,inClassInstructor)
        utilsSetUpClassDescription(this,inClassDescription)
        utilsSetUpBookAndCancelButton(this,userId,scheduleId, inClassStatus)

    }

    fun btnBookClass(view: View) {
        /**
         * Dispalys error if user membership is not active
         * Displays error if user is restricted to book class
         * if valid then show dialog
         */

        val classAvailabilityFor = intent.getStringExtra("classAvailabilityFor") ?: ""
        utilsValidateUserMembershipAndGender(
            this,
            classAvailabilityFor,
            userId
        ){
            showBookClassDialog()
        }
    }

    fun btnCancelBooking(view: View) {
        showCancelClassDialog()
    }

    private fun showBookClassDialog() {

        /**
         * Displays a confirmation dialog for booking a class.
         * Users can confirm or cancel the booking.
         */

        val dialogView = LayoutInflater.from(this).inflate(R.layout.book_class_dialog_box, null)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()


        val btnCancelBooking = dialogView.findViewById<MaterialButton>(R.id.btnCancelBooking)
        val btnConfirmBooking = dialogView.findViewById<MaterialButton>(R.id.btnConfirmBooking)


        btnCancelBooking.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirmBooking.setOnClickListener {
            alertDialog.dismiss()
            createClassBookingForUser()
        }

    }

    private fun createClassBookingForUser(){
        val validUserId = userId ?: return
        val validClassId = classId ?: return
        val validScheduleId = scheduleId

        userFirebaseHelper.addUserBookedClassToCurrentBookings( validUserId, validClassId,validScheduleId) { success ->
            if (success) {
                addBookingToSchedules(validScheduleId,validUserId)
                val currentBookingCount = mapOf("classCurrentBookings" to currBookings+1)
                utilsUpdateClassCurrentBookings(validScheduleId, currentBookingCount) { success ->
                    if (success){
                        Toast.makeText(this, "Successfully booked the class", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }else {
                Toast.makeText(this, "Failed to book the class", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addBookingToSchedules(scheduleId: String, userId: String) {
        val booking = booking(
            userId = userId,
            scheduleId = scheduleId
        )
        scheduleFirebaseHelper.newAddUserBookingToSchedules( scheduleId, userId, booking) { success ->
            if (success) {
                println("Booking added to schedule successfully.")
            }else {
                println("Failed to add booking to schedule.")
            }
        }
    }

    private fun cancelUserCurrentBooking() {
        val validUserId = userId ?: return
        val validClassId = classId ?: return
        val validScheduleId = scheduleId

        /*
           Delete Bookings Info from users collection
        */
        firebaseHelper.deleteUserCurrentBookingById(validUserId, validScheduleId) { success ->
            if (!success) {
                Toast.makeText(this, "Failed to delete booking or booking not found.", Toast.LENGTH_LONG).show()
                return@deleteUserCurrentBookingById
            }

            /*
            Delete User ID, Schedule ID from schedules collection
             */
            scheduleFirebaseHelper.deleteUserBookingsFromSchedule(validScheduleId, validUserId) { deletionSuccess ->
                if (!deletionSuccess) {
                    Toast.makeText(this, "Failed to remove from schedule bookings.", Toast.LENGTH_LONG).show()
                    return@deleteUserBookingsFromSchedule
                }
                /*
                  decrements bookings to -1, when user cancels
                */
                val updatedBookingCount = mapOf("classCurrentBookings" to currBookings - 1)
                firebaseHelper.decrementClassCurrentBookings(validScheduleId, updatedBookingCount) { decrementSuccess ->
                    if (decrementSuccess) {
                        Toast.makeText(this, "Your booking has been successfully canceled.", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, "An error occurred while updating bookings. Please contact support!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    private fun showCancelClassDialog() {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.cancel_class_dialog_box, null)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()


        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.btnCancelDialogBox)
        val btnConfirmCancellation = dialogView.findViewById<MaterialButton>(R.id.btnConfirmCancellation)


        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirmCancellation.setOnClickListener {
            alertDialog.dismiss()
            cancelUserCurrentBooking()
        }
    }

    private fun getCurrentUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}