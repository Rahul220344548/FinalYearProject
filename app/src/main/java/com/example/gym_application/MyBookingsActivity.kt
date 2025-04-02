package com.example.gym_application

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
import com.example.gym_application.admin_view.adapter.AdminBookingListAdapter
import com.example.gym_application.controller.AllUserBookingsAdapter
import com.example.gym_application.controller.ScheduleFirebaseHelper
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.newModel.NewSchedule
import com.google.firebase.auth.FirebaseAuth
import helper.FirebaseClassesHelper
@RequiresApi(Build.VERSION_CODES.O)
class MyBookingsActivity : AppCompatActivity() {

    private lateinit var txtNoClassBookings : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : AllUserBookingsAdapter

    private val userFirebaseHelper = UserFirebaseDatabaseHelper()
    private val scheduleFirebaseHelper = ScheduleFirebaseHelper()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_bookings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtNoClassBookings = findViewById(R.id.txtNoClassBookings)

        val toolbar: Toolbar = findViewById(R.id.admin_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        recyclerView = findViewById(R.id.userAllBookingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AllUserBookingsAdapter(emptyList())
        recyclerView.adapter = adapter

        observeUserCurrentBookingsLive()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeUserCurrentBookingsLive() {
        userFirebaseHelper.listenToUserCurrentBookingsLive(userId ?: "") { bookings ->
            if (bookings.isNotEmpty()) {
                fetchAllBookedClasses(bookings)
            }else {
                txtNoClassBookings.visibility = View.VISIBLE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchAllBookedClasses(scheduleIds: List<String>) {
        val liveSchedules = mutableListOf<NewSchedule>()
        scheduleIds.forEach { scheduleId ->
            scheduleFirebaseHelper.listenToBookedSchedulesFullDetail(scheduleId) { updatedSchedule ->
                if (updatedSchedule != null) {
                    val existingIndex = liveSchedules.indexOfFirst { it.scheduleId == updatedSchedule.scheduleId }
                    if (existingIndex >= 0) {
                        liveSchedules[existingIndex] = updatedSchedule
                    } else {
                        liveSchedules.add(updatedSchedule)
                    }
                    adapter.updateData(liveSchedules)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}