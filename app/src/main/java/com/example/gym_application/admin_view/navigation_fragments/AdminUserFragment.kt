package com.example.gym_application.admin_view.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminUserListAdapter
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.model.UserDetails


class AdminUserFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminUserListAdapter

    private lateinit var searchView: SearchView

    private val userFirebaseHelper = UserFirebaseDatabaseHelper()
    private var userList : List<UserDetails> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_user, container, false)

        recyclerView = view.findViewById(R.id.adminUsersListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = AdminUserListAdapter(emptyList())
        recyclerView.adapter = adapter

        fetchUsersList()

        return view
    }

    private fun fetchUsersList() {

        userFirebaseHelper.listenForUserUpdates { fetchedUserList ->
            if (fetchedUserList.isNotEmpty()) {
                userList = fetchedUserList
                adapter.updateData(userList)
            }else {
                Toast.makeText(context ,"Error fetching classes", Toast.LENGTH_SHORT).show()
            }
        }


    }
}