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
import com.example.gym_application.admin_view.adapter.AdminUserListAdapter
import com.example.gym_application.adapter.UserFirebaseDatabaseHelper
import com.example.gym_application.model.UserDetails


class AdminUserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminUserListAdapter

    private lateinit var searchView: SearchView

    private val userFirebaseHelper = UserFirebaseDatabaseHelper()

    private var userList : List<UserDetails> = emptyList()
    private val userIdMap: MutableMap<UserDetails, String> = mutableMapOf()

    private lateinit var goToCreateStaffBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_admin_users, container, false)

        goToCreateStaffBtn = view.findViewById(R.id.btnAddStaff)

        recyclerView = view.findViewById(R.id.adminUsersListRecyclerView)
        searchView = view.findViewById(R.id.findUsersSearchView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = AdminUserListAdapter(mutableListOf(), mutableMapOf())
        recyclerView.adapter = adapter

        fetchUsersList()

        goToCreateStaffBtn.setOnClickListener {
            btnToNavigateToStaffCreation()
        }

        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })


        return view
    }

    private fun fetchUsersList() {

        userFirebaseHelper.listenForUserUpdates { fetchedUserMap ->
            if (fetchedUserMap.isNotEmpty()) {
                userList = fetchedUserMap.keys.toList()
                userIdMap.clear()
                userIdMap.putAll(fetchedUserMap)
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

    private fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            adapter.updateData(userList, userIdMap)
        }else {

            val filteredList = userList.filter {
                "${it.firstName} ${it.lastName}".contains(query.trim(), ignoreCase = true)
            }
            val filteredUserIdMap = userIdMap.filterKeys { it in filteredList }

            adapter.updateData(filteredList, filteredUserIdMap)

        }
    }

}