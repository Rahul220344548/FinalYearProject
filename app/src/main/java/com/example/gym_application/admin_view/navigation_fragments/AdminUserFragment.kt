package com.example.gym_application.admin_view.navigation_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.AdminAddStaff
import com.example.gym_application.admin_view.CreateAClass
import com.example.gym_application.admin_view.adapter.AdminUserListAdapter
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.model.UserDetails


class AdminUserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminUserListAdapter

    private lateinit var searchView: SearchView

    private val userFirebaseHelper = UserFirebaseDatabaseHelper()
    private var userList : List<UserDetails> = emptyList()

    private lateinit var goToCreateStaffBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_admin_users, container, false)

        goToCreateStaffBtn = view.findViewById(R.id.btnAddStaff)

        recyclerView = view.findViewById(R.id.adminUsersListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = AdminUserListAdapter(mutableListOf(), mutableMapOf())
        recyclerView.adapter = adapter

        fetchUsersList()

        goToCreateStaffBtn.setOnClickListener {
            btnToNavigateToStaffCreation()
        }

        return view
    }

    private fun fetchUsersList() {

        userFirebaseHelper.listenForUserUpdates { fetchedUserMap ->
            if (fetchedUserMap.isNotEmpty()) {
                val userList = fetchedUserMap.keys.toList()
                val userIdMap = fetchedUserMap
                adapter.updateData(userList, userIdMap)
            }else {
                Toast.makeText(context ,"No Users found", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun btnToNavigateToStaffCreation() {
        val intent = Intent(requireActivity(), AdminAddStaff::class.java)
        startActivity(intent)
    }

}