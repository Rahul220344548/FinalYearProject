package com.example.gym_application

import FirebaseDatabaseHelper
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_class_detail_view)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        database = FirebaseDatabase.getInstance()
        classRef = database.getReference("classes")

        setClassDetailInfo()
    }


    fun btnBookClass(view: View) {

        // gets current user ID
        val userId = getCurrentUserId()

        // if user membershipstatus is not active then return
        // if user gender does not matches with class gender then return

        showBookClassDialog()

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
        val userId = getCurrentUserId()
        val classId = intent.getStringExtra("classId");

        createClassBookingForUser(userId,classId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setClassDetailInfo() {
        val classId = intent.getStringExtra("classId");

        setUpScheduleInfo()
        setUpClassAvailableFor()
        setUpClassStatus()
        setUpClassLocation()
        setUpClassInstructor()
        setUpClassInstructor()
        setUpClassDescription()
        setUpClassBookButton(classId)
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

        val currBookings = intent.getIntExtra("classCurrentBookings",0)
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
        val currBookings = intent.getIntExtra("classCurrentBookings",0)
        val currentBookingCount = mapOf("classCurrentBookings" to currBookings+1)

        classRef.child(classId?:"").updateChildren(currentBookingCount)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createClassBookingForUser(userId: String?, classId: String?){
        database.reference.child("users").child(userId?:"").child("bookings").child("current").push().setValue(UserClassBooking(classId?:""))
            .addOnSuccessListener {
                updateClassCurrentBooking(classId)
                onBackPressed()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to book the class: ${exception.message}", Toast.LENGTH_LONG).show()
                exception.printStackTrace()
            }
    }

    //diable button for user who are already booked
    private fun setUpClassBookButton(classId: String?) {

        val btnBookClass = findViewById<Button>(R.id.btnBookClass)
        val bookingConfirmTextView = findViewById<TextView>(R.id.bookingConfirmationText)
        val btnCancelClass = findViewById<Button>(R.id.btnCancelClassBooking)

        hasUserAlreadyBookedThisClass(classId ?: "") { isBooked ->
            if (isBooked) {
                bookingConfirmTextView.visibility = View.VISIBLE
                btnBookClass.visibility = View.GONE
                btnCancelClass.visibility = View.VISIBLE
//                btnBookClass.isClickable = false
//                btnBookClass.isEnabled = false
//                btnBookClass.text = "Already Booked"
            } else {
                btnBookClass.isClickable = true
                btnBookClass.isEnabled = true
                btnBookClass.text = "Book Class"
            }
        }
    }


    private fun hasUserAlreadyBookedThisClass(classId: String, callback: (Boolean) -> Unit) {

        val userId = getCurrentUserId().toString()
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
            .child(userId)
            .child("bookings")
            .child("current")

        databaseReference.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                var isBooked = false

                for (dataSnapshot in snapshot.children) {
                    val userClassBooking = dataSnapshot.getValue(UserClassBooking::class.java)
                    if (userClassBooking?.classId == classId) {
                        isBooked = true
                        break
                    }
                }
                callback(isBooked)
            }else {
                callback(false)
            }
        }.addOnFailureListener {
            println("Error fetching data: ${it.message}")
            callback(false)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}