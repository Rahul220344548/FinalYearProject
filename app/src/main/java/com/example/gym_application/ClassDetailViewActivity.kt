package com.example.gym_application

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
import com.example.gym_application.utils.ValidationClassCreation
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_class_detail_view)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

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
        Toast.makeText(this, "Booking confirmed for user: $userId", Toast.LENGTH_SHORT).show()

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
            txtclassAvailability.text = classAvailabilityFor
        }else {
            txtclassAvailability.text = "$classAvailabilityFor only"
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
        txtClassTitle.text = inClassTitle
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
        return user?.uid // Returns the unique Firebase UID or null if not logged in
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}