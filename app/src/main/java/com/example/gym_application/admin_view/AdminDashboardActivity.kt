package com.example.gym_application.admin_view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem

import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import com.example.gym_application.MainActivity
import com.example.gym_application.R
import com.example.gym_application.admin_view.navigation_fragments.AdminClassesFragment
import com.example.gym_application.admin_view.navigation_fragments.AdminHomeFragment
import com.example.gym_application.admin_view.navigation_fragments.AdminMembershipFragment
import com.example.gym_application.admin_view.navigation_fragments.AdminFAQFragment
import com.example.gym_application.admin_view.navigation_fragments.AdminUserFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity(), OnNavigationItemSelectedListener{

    private lateinit var drawerLayout : DrawerLayout

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)
        auth = FirebaseAuth.getInstance()

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.open_admin_nav, R.string.close_admin_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            replaceFragment(AdminHomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_home -> replaceFragment(AdminHomeFragment())
            R.id.nav_users -> replaceFragment(AdminUserFragment())
            R.id.nav_clases -> replaceFragment(AdminClassesFragment())
            R.id.nav_membership -> replaceFragment(AdminMembershipFragment())
            R.id.nav_payment-> replaceFragment(AdminFAQFragment())
                R.id.nav_logout-> {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment (fragment: Fragment) {

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}