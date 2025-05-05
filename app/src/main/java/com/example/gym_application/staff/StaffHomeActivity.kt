package com.example.gym_application.staff

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.gym_application.R
import com.google.android.material.bottomnavigation.BottomNavigationView

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
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