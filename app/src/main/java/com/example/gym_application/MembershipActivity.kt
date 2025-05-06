package com.example.gym_application

import MembershipAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.model.MembershipPlans
import com.example.gym_application.newModel.NewMembershipPlan
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import helper.FirebaseMemebershipHelper

class MembershipActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MembershipAdapter
    private var selectedPlan: NewMembershipPlan? = null
    private val firebaseMembershipHelper = FirebaseMemebershipHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)



        val toolbar: Toolbar = findViewById(R.id.user_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        setUpRecyclerView()
        fetchMembershipList()

        val continueButton = findViewById<Button>(R.id.btnContinueToPayment)
        continueButton.setOnClickListener {
            if (selectedPlan != null) {
                val intent = Intent(this, MembershipPaymentActivity::class.java).apply {
                    putExtra("id", selectedPlan!!.id)
                    putExtra("planTitle", selectedPlan!!.title)
                    putExtra("planDuration", selectedPlan!!.duration)
                    putExtra("planPrice", selectedPlan!!.price)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a plan first!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun setUpRecyclerView() {
        recyclerView = findViewById(R.id.displayMembershipPlans)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MembershipAdapter(emptyList()) { selected ->
            selectedPlan = selected
        }
        recyclerView.adapter = adapter

    }

    /*
    Gets all membership plans from firebase
    update.date populates the recycler view
     */

    private fun fetchMembershipList() {
        firebaseMembershipHelper.fetchAllMembershipPlansAsList { plans ->
            adapter.updateData(plans)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
