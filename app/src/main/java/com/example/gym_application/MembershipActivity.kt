package com.example.gym_application

import MembershipAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private val database = FirebaseDatabase.getInstance().getReference("membershipPlan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                adapter = MembershipAdapter(plans) { selectedPlan ->
                    Toast.makeText(
                        this@MembershipActivity,
                        "Selected: ${selectedPlan.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MembershipActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
