package com.example.gym_application.staff

import android.os.Build
import android.os.Bundle
import android.view.View
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
import com.example.gym_application.model.UserDetails
import com.example.gym_application.utils.formatDateUtils
import helper.FirebaseClassesHelper

@RequiresApi(Build.VERSION_CODES.O)

class StaffScheduleInfoActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : AdminBookingListAdapter

    private lateinit var classTitle: TextView
    private lateinit var classDate: TextView
    private lateinit var classTimes: TextView
    private lateinit var classInstructor: TextView
    private lateinit var classLocation : TextView
    private lateinit var classRemainingSpots : TextView
    private lateinit var showNoBookings : TextView

    private lateinit var classId: String
    private lateinit var scheduleId : String

    private val firebaseHelper = FirebaseClassesHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_staff_schedule_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar: Toolbar = findViewById(R.id.staff_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        recyclerView = findViewById(R.id.adminBookedClassListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminBookingListAdapter(emptyList())
        recyclerView.adapter = adapter

        scheduleId = intent.getStringExtra("scheduleId")?:""
        classId = intent.getStringExtra("classId")?:""

        initalizeTextFields()
        fetchBookingForSchedule()

    }

    private fun initalizeTextFields() {

        val inClassTitle = intent.getStringExtra("classTitle")
        val inClassStartDate = intent.getStringExtra("classStartDate") ?: "01/01/2000"
        val inClassStartTime = intent.getStringExtra("classStartTime") ?: "00:00"
        val inClassEndTime = intent.getStringExtra("classEndTime") ?: "00:00"
        val maxCapacity = intent.getIntExtra("classMaxCapacity",0)
        val currBookings = intent.getIntExtra("classCurrentBookings",0)
        val inClassLocation = intent.getStringExtra("classLocation") ?: ""
        val inClassInstructor = intent.getStringExtra("classInstructor") ?: ""
        val inClassStatus = intent.getStringExtra("status") ?: ""

        classTitle = findViewById<TextView>(R.id.staffCurrentScheduleTitle)
        classTitle.text = inClassTitle


        classDate = findViewById<TextView>(R.id.staffCurrentScheduleDates)
        classDate.text = formatDateUtils.adminFormattedDate(inClassStartDate)

        classTimes  = findViewById<TextView>(R.id.staffCurrentScheduleTimes)
        classTimes.text = "${inClassStartTime} - ${inClassEndTime}"

        classInstructor = findViewById<TextView>(R.id.staffCurrentInstructor)
        classInstructor.text = inClassInstructor

        classLocation = findViewById<TextView>(R.id.staffCurrentLocation)
        classLocation.text = inClassLocation

        classRemainingSpots = findViewById<TextView>(R.id.staffCurrentBookings)
        classRemainingSpots.setText("${currBookings.toString()} / ${maxCapacity.toString()}")

    }

    private fun fetchBookingForSchedule() {

        firebaseHelper.getUserIdsForSchedule(scheduleId) { userIds ->
            if (userIds.isNotEmpty()) {
                fetchUserDetails(userIds)
            } else {
                adapter.updateData(emptyList())
                showNoBookings = findViewById<TextView>(R.id.stafftxtNoBookingsMade)
                showNoBookings.visibility = View.VISIBLE
                adapter.updateData(emptyList())
            }
        }

    }

    private fun fetchUserDetails(userIds: List<String>) {
        /*
        Once UserIDs are fetched
        UserDetails is fetched as a List matched with UserID
         */
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