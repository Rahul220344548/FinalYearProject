package com.example.gym_application.staff

import GymClassesAdapter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.AccountFragment
import com.example.gym_application.CalendarAdapter
import com.example.gym_application.ClassesFragment
import com.example.gym_application.HomeFragment
import com.example.gym_application.R
import com.example.gym_application.controller.ScheduleFirebaseHelper
import com.example.gym_application.utils.CalendarUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class StaffHomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_staff_home)
        bottomNavigationView = findViewById(R.id.staff_bottom_navigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.staff_bottom_schedule -> {
                    replaceFragment(StaffSchedulesFragment())
                    true
                }
                R.id.staff_bottom_profile -> {
                    replaceFragment(StaffProfileFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(StaffSchedulesFragment())
        bottomNavigationView.requestLayout()
    }


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.staff_frame_container, fragment).commit()
    }

    fun navigateToClassesTab() {
        bottomNavigationView.selectedItemId = R.id.staff_bottom_schedule
    }


}