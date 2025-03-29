package com.example.gym_application.admin_view

import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminBookingListAdapter
import com.example.gym_application.admin_view.adapter.AdminClassListAdapter
import com.example.gym_application.model.UserDetails
import com.example.gym_application.utils.formatDateUtils
import com.google.firebase.database.FirebaseDatabase
import helper.FirebaseBookingsHelper

class AdminScheduleInfo : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : AdminBookingListAdapter

    private lateinit var classTitle: TextView
    private lateinit var classDate: TextView
    private lateinit var classTimes: TextView
    private lateinit var classInstructor: TextView
    private lateinit var classLocation : TextView
    private lateinit var classRemainingSpots : TextView

    private lateinit var classId: String
    private lateinit var scheduleId : String

    private val firebaseHelper = FirebaseBookingsHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_schedule_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        recyclerView = findViewById(R.id.adminBookedClassListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminBookingListAdapter(emptyList())
        recyclerView.adapter = adapter

        scheduleId = intent.getStringExtra("scheduleId")?:""
        classId = intent.getStringExtra("classId")?:""

        initialTextFields()

        fetchBookingForSchedule()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initialTextFields() {

        val inClassTitle = intent.getStringExtra("classTitle")
        val inClassStartDate = intent.getStringExtra("classStartDate") ?: "01/01/2000"
        val inClassStartTime = intent.getStringExtra("classStartTime") ?: "00:00"
        val inClassEndTime = intent.getStringExtra("classEndTime") ?: "00:00"
        val maxCapacity = intent.getIntExtra("classMaxCapacity",0)
        val currBookings = intent.getIntExtra("classCurrentBookings",0)
        val inClassLocation = intent.getStringExtra("classLocation") ?: ""
        val inClassInstructor = intent.getStringExtra("classInstructor") ?: ""

        classTitle = findViewById<TextView>(R.id.adminCurrentScheduleTitle)
        classTitle.text = inClassTitle


        classDate = findViewById<TextView>(R.id.adminCurrentScheduleDates)
        classDate.text = formatDateUtils.adminFormattedDate(inClassStartDate)

        classTimes  = findViewById<TextView>(R.id.adminCurrentScheduleTimes)
        classTimes.text = "${inClassStartTime} - ${inClassEndTime}"

        classInstructor = findViewById<TextView>(R.id.adminCurrentInstructor)
        classInstructor.text = inClassInstructor

        classLocation = findViewById<TextView>(R.id.adminCurrentLocation)
        classLocation.text = inClassLocation

        classRemainingSpots = findViewById<TextView>(R.id.adminCurrentBookings)
        classRemainingSpots.setText("${currBookings.toString()} / ${maxCapacity.toString()}")

    }



    private fun fetchBookingForSchedule() {

        firebaseHelper.getUserIdsForSchedule(scheduleId) { userIds ->
            if (userIds.isNotEmpty()) {
                fetchUserDetails(userIds)
            } else {
                adapter.updateData(emptyList())
            }
        }

    }

    /*
    Once UserIDs are fetched
    UserDetails is fetched as a List matched with UserID
     */
    private fun fetchUserDetails(userIds: List<String>) {
        val userList = mutableListOf<UserDetails>()
        var usersFetched = 0

        for (userId in userIds) {
            firebaseHelper.getUserDetailsById(userId) { userDetails ->
                userDetails?.let {
                    userList.add(it)
                }
                usersFetched++
                if (usersFetched == userIds.size) {
                    adapter.updateData(userList)
                }
            }
        }
    }




    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}