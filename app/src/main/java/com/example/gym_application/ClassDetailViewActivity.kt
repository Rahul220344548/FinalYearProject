package com.example.gym_application

import android.os.Bundle
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.utils.ValidationClassCreation
import org.w3c.dom.Text

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

        setClassDetailInfo()

    }


    private fun btnBookClass() {

    }

    private fun setClassDetailInfo() {

        val currBookings = intent.getIntExtra("classCurrentBookings",0)
        val maxCapacity = intent.getIntExtra("classMaxCapacity",0)
        val remainingSpots = maxCapacity - currBookings

        val inClassLocation = intent.getStringExtra("classLocation")

        val inClassInstructor = intent.getStringExtra("classInstructor")

        val inClassDescription = intent.getStringExtra("classDescription")





        txtClassRemainingSpot =findViewById<TextView>(R.id.classRemainingSpots)
        txtClassRemainingSpot.text = "Available ($remainingSpots spots left)"

        txtClassLocation = findViewById<TextView>(R.id.classLocation)
        txtClassLocation.text = inClassLocation

        txtClassInstructor =findViewById<TextView>(R.id.classInstructor)
        txtClassInstructor.text = inClassInstructor

        txtClassDescription = findViewById<TextView>(R.id.classDescription)
        txtClassDescription.text = inClassDescription



    }

    private fun setUpScheduleInfo() {

        val inClassTitle = intent.getStringExtra("classTitle")
        val inClassStartDate = intent.getStringExtra("classStartDate")

        val inClassStartTime = intent.getStringExtra("classStartTime") ?: "00:00"
        val inClassEndTime = intent.getStringExtra("classEndTime") ?: "00:00"

        val startMinutes = ValidationClassCreation.convertTimeToMinutes(inClassStartTime)
        val endMinutes = ValidationClassCreation.convertTimeToMinutes(inClassEndTime)
        val duration = endMinutes - startMinutes

        txtClassTitle = findViewById<TextView>(R.id.classTitle)
        txtClassTitle.text = inClassTitle

        txtClassScheduledDate = findViewById<TextView>(R.id.classScheduledDate)
        txtClassScheduledDate.text = inClassStartDate

        txtClassScheduledTime = findViewById<TextView>(R.id.classScheduledTime)
        txtClassScheduledTime.text = "$inClassStartTime - $inClassEndTime"

        txtclassLength = findViewById<TextView>(R.id.classLength)
        txtclassLength.text = "$duration min"

    }

    private fun setUpClassAvailableFor() {
        val classAvailabilityFor = intent.getStringExtra("classGenderRestrictions")
        txtclassAvailability = findViewById<TextView>(R.id.classAvailabilityFor)
        txtclassAvailability.text = classAvailabilityFor


    }

    private fun setUpClassStatus() {

        val currBookings = intent.getIntExtra("classCurrentBookings",0)
        val maxCapacity = intent.getIntExtra("classMaxCapacity",0)
        val remainingSpots = maxCapacity - currBookings
        txtClassRemainingSpot =findViewById<TextView>(R.id.classRemainingSpots)
        txtClassRemainingSpot.text = "Available ($remainingSpots spots left)"

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}