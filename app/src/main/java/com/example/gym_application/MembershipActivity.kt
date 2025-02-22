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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MembershipActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MembershipAdapter
    private var selectedPlan: MembershipPlans? = null
    private val database = FirebaseDatabase.getInstance().getReference("membershipPlan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)


        val toolbar: Toolbar = findViewById(R.id.user_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val continueButton = findViewById<Button>(R.id.btnContinueToPayment)
        continueButton.setOnClickListener {
            if (selectedPlan != null) {
                val intent = Intent(this, MembershipPaymentActivity::class.java)
                intent.putExtra("planTitle", selectedPlan?.title)
                intent.putExtra("planDuration", selectedPlan?.duration)
                intent.putExtra("planPrice", selectedPlan?.price)
                startActivity(intent)
            }else {
                Toast.makeText(this, "Please select a plan first!", Toast.LENGTH_SHORT).show()
            }
        }

        fetchPlans()
    }

    private fun fetchPlans() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val plans = mutableListOf<MembershipPlans>()
                for (planSnapshot in snapshot.children) {
                    val plan = planSnapshot.getValue(MembershipPlans::class.java)
                    if (plan != null) {
                        plans.add(plan)
                    }
                }

                if (::adapter.isInitialized) {
                    adapter.notifyDataSetChanged()  // Update adapter if it already exists
                } else {
                    adapter = MembershipAdapter(plans) { selected ->
                        selectedPlan = selected
                    }
                    recyclerView.adapter = adapter  // Attach adapter only after data is available
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MembershipActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
