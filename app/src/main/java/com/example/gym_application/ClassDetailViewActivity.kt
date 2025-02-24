package com.example.gym_application

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.utils.ValidationClassCreation
import org.w3c.dom.Text
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


    private fun btnBookClass() {

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
        val classAvailabilityFor = intent.getStringExtra("classGenderRestrictions")
        txtclassAvailability = findViewById<TextView>(R.id.classAvailabilityFor)
        if (classAvailabilityFor == "None") {
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
        txtClassRemainingSpot.text = "Available ($remainingSpots spots left)"
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
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}