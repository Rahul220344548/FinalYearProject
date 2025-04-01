package com.example.gym_application.admin_view.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminMembershipListAdapter
import com.example.gym_application.model.MembershipPlans
import com.example.gym_application.utils.MembershipUtils
import com.example.gym_application.utils.OnMembershipClickListener
import helper.FirebaseMemebershipHelper


class AdminMembershipFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminMembershipListAdapter

    private var selectedDuration: String = ""

    private val firebaseMembershipHelper = FirebaseMemebershipHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_admin_membership, container, false)

        recyclerView = view.findViewById(R.id.adminMembershipListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = AdminMembershipListAdapter(emptyList())
        recyclerView.adapter = adapter

        fetchMembershipList()

        return view
    }


    private fun fetchMembershipList() {
        firebaseMembershipHelper.fetchAllMembershipPlansAsList { plans ->
            adapter.updateData(plans)
        }
    }




    private fun showEditDialog(membership: MembershipPlans) {

//        var selectedDuration = membership.duration  // To hold selected duration
//
//        val alertDialog = MembershipUtils.createEditMembershipDialog(requireContext(), membership, { duration ->
//            selectedDuration = duration
//        })


    }



}