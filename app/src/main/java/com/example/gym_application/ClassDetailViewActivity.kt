package com.example.gym_application

import FirebaseDatabaseHelper
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gym_application.model.UserClassBooking
import com.example.gym_application.utils.ClassBookingUtils
import com.example.gym_application.utils.ValidationClassCreation
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



@RequiresApi(Build.VERSION_CODES.O)
class ClassDetailViewActivity : AppCompatActivity() {

    private lateinit var txtClassTitle : TextView
    private lateinit var txtClassScheduledDate : TextView
    private lateinit var txtClassScheduledTime : TextView
    private lateinit var txtclassLength : TextView
    private lateinit var txtclassAvailability: TextView
    private lateinit var txtClassRemainingSpot: TextView
    private lateinit var txtClassLocation : TextView
    private lateinit var txtClassInstructor : TextView
    private lateinit var txtClassDescription : TextView

    private lateinit var database: FirebaseDatabase
    private lateinit var classRef: DatabaseReference

    private val userId = getCurrentUserId().toString()
    private val firebaseHelper = FirebaseDatabaseHelper()
    private lateinit var classId: String
    private var currBookings: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_class_detail_view)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        classId = intent.getStringExtra("classId")?:""
        currBookings = intent.getIntExtra("classCurrentBookings",0)

        database = FirebaseDatabase.getInstance()
        classRef = database.getReference("classes")

        setClassDetailInfo()
    }

    fun btnBookClass(view: View) {
        validateUserAndBookClass()

    }

    fun btnCancelBooking(view: View) {
        showCancelClassDialog()
    }

    private fun cancelUserCurrentBooking() {
        val currentBookingCount = mapOf("classCurrentBookings" to currBookings-1)

        firebaseHelper.deleteUserCurrentBookingById(userId, classId?:"") { success ->
            if (success) {
                Toast.makeText(this, "Your booking has been successfully canceled.", Toast.LENGTH_LONG).show()
                firebaseHelper.decrementClassCurrentBookings(classId?:"", currentBookingCount) { decrementSuccess ->
                    if (decrementSuccess) {

                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("navigateToClassesFragment", true)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        println("An Error cause: pLEASE CALL ADMIn")
                    }
                }
            } else {
                println("Failed to delete booking or booking not found.")
            }
        }
    }


    private fun validateUserAndBookClass() {
        firebaseHelper.getUserMembershipStatus(userId) { membershipStatus ->
            if ( membershipStatus != "active") {
                Toast.makeText(this, "Activate your membership to book this class!"
                    , Toast.LENGTH_LONG).show()
                return@getUserMembershipStatus
            }
            val classAvailabilityFor = intent.getStringExtra("classAvailabilityFor").toString();
            firebaseHelper.getUserGender(userId) { userGender ->
                if (userGender != null) {
                    if (ClassBookingUtils.canUserBookClass(classAvailabilityFor,userGender)) {
                        showBookClassDialog()
                    } else {
                        Toast.makeText(this, "This class is only available for $classAvailabilityFor participants."
                            , Toast.LENGTH_LONG).show()
                        return@getUserGender
                    }
                } else {
                    println("Unable to fetch user gender. Please try again.")
                }
            }
        }

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
            confirmClassBooking()
        }

    }

    private fun confirmClassBooking() {
        createClassBookingForUser()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClassDetailInfo() {
        setUpScheduleInfo()
        setUpClassAvailableFor()
        setUpClassStatus()
        setUpClassLocation()
        setUpClassInstructor()
        setUpClassInstructor()
        setUpClassDescription()
        setUpBookAndCancelButton()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpScheduleInfo() {
        setUpClassTitle()
        setUpClassStartDate()
        setUpClassTimes()
        setUpClassDuration()
    }

    private fun setUpClassAvailableFor() {
        val classAvailabilityFor = intent.getStringExtra("classAvailabilityFor")
        txtclassAvailability = findViewById<TextView>(R.id.classAvailabilityFor)
        if (classAvailabilityFor == "All") {
            txtclassAvailability.text = classAvailabilityFor?.toUpperCase()
        }else {
            txtclassAvailability.text = "$classAvailabilityFor only"?.toUpperCase()
        }
    }

    private fun setUpClassStatus() {

        val maxCapacity = intent.getIntExtra("classMaxCapacity",0)
        val remainingSpots = maxCapacity - currBookings
        txtClassRemainingSpot =findViewById<TextView>(R.id.classRemainingSpots)
        val btnBookClass = findViewById<Button>(R.id.btnBookClass)
        // if class is full
        if (currBookings >= maxCapacity) {
            txtClassRemainingSpot.setTextColor(ContextCompat.getColor(this,R.color.red))
            txtClassRemainingSpot.text = "Class Full"
            btnBookClass.isEnabled = false
            // hide button btnBookClass.visibility = View.INVISIBLE
            return
        }
        txtClassRemainingSpot.text = "Available ($remainingSpots spots left)"
        txtClassRemainingSpot.setTextColor(ContextCompat.getColor(this, R.color.available_green))
        btnBookClass.isEnabled = true

    }

    private fun setUpClassLocation() {
        val inClassLocation = intent.getStringExtra("classLocation")
        txtClassLocation = findViewById<TextView>(R.id.classLocation)
        txtClassLocation.text = inClassLocation

    }

    private fun setUpClassInstructor() {

        val inClassInstructor = intent.getStringExtra("classInstructor")
        txtClassInstructor =findViewById<TextView>(R.id.classInstructor)
        txtClassInstructor.text = inClassInstructor

    }

    private fun setUpClassDescription() {
        val inClassDescription = intent.getStringExtra("classDescription")
        txtClassDescription = findViewById<TextView>(R.id.classDescription)
        txtClassDescription.text = inClassDescription
    }

    private fun setUpClassTitle(){

        val inClassTitle = intent.getStringExtra("classTitle")
        txtClassTitle = findViewById<TextView>(R.id.classTitle)
        txtClassTitle.text = inClassTitle?.toUpperCase()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpClassStartDate() {
        val inClassStartDate = intent.getStringExtra("classStartDate") ?: "01/01/2000"
        txtClassScheduledDate = findViewById<TextView>(R.id.classScheduledDate)
        val formatDate = ValidationClassCreation.formatDate(inClassStartDate)
        txtClassScheduledDate.text = formatDate
    }
    private fun setUpClassTimes() {
        val inClassStartTime = intent.getStringExtra("classStartTime") ?: "00:00"
        val inClassEndTime = intent.getStringExtra("classEndTime") ?: "00:00"

        txtClassScheduledTime = findViewById<TextView>(R.id.classScheduledTime)
        txtClassScheduledTime.text = "$inClassStartTime - $inClassEndTime"

    }
    private fun setUpClassDuration() {

        val inClassStartTime = intent.getStringExtra("classStartTime") ?: "00:00"
        val inClassEndTime = intent.getStringExtra("classEndTime") ?: "00:00"

        val startMinutes = ValidationClassCreation.convertTimeToMinutes(inClassStartTime)
        val endMinutes = ValidationClassCreation.convertTimeToMinutes(inClassEndTime)
        val duration = endMinutes - startMinutes

        txtclassLength = findViewById<TextView>(R.id.classLength)
        txtclassLength.text = "$duration min"


    }

    private fun getCurrentUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

    private fun updateClassCurrentBooking(classId: String?){
        val currentBookingCount = mapOf("classCurrentBookings" to currBookings+1)

        classRef.child(classId?:"").updateChildren(currentBookingCount)
            .addOnSuccessListener {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()

            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createClassBookingForUser(){
        database.reference.child("users").child(userId?:"").child("bookings").child("current").push().setValue(UserClassBooking(classId?:""))
            .addOnSuccessListener {
                updateClassCurrentBooking(classId)
                Toast.makeText(this, "Successfully booked the class", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to book the class: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    private fun setUpBookAndCancelButton() {
        val btnBookClass = findViewById<Button>(R.id.btnBookClass)
        val bookingConfirmTextView = findViewById<TextView>(R.id.bookingConfirmationText)
        val btnCancelClass = findViewById<Button>(R.id.btnCancelClassBooking)

        firebaseHelper.hasUserAlreadyBookedThisClass(userId,classId ?: "") { isBooked ->
            if (isBooked) {
                bookingConfirmTextView.visibility = View.VISIBLE
                btnBookClass.visibility = View.GONE
                btnCancelClass.visibility = View.VISIBLE
            } else {
                btnBookClass.isClickable = true
                btnBookClass.isEnabled = true
                btnBookClass.text = "Book Class"
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}