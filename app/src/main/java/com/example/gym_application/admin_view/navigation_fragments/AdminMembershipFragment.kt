package com.example.gym_application.admin_view.navigation_fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminMembershipListAdapter
import com.example.gym_application.controller.AllUserBookingsAdapter
import com.example.gym_application.model.MembershipPlans
import com.example.gym_application.utils.OnMembershipClickListener
import com.example.gym_application.utils.utilsSetUpSelectMembershipDurationDropdown
import helper.FirebaseMemebershipHelper


class AdminMembershipFragment : Fragment(),
    OnMembershipClickListener {

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

        adapter = AdminMembershipListAdapter(requireContext(),emptyList(), this)
        recyclerView.adapter = adapter

        fetchMembershipList()

        return view
    }


    private fun fetchMembershipList() {
        firebaseMembershipHelper.fetchAllMembershipPlansAsList { plans ->
            adapter.updateData(plans)
        }
    }


    override fun onMembershipClicked(membership: MembershipPlans) {
        showEditDialog(membership)
    }

    private fun showEditDialog(membership: MembershipPlans) {
        val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_edit_membership_plan, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialog)
            .setCancelable(false)

        dialog.findViewById<TextView>(R.id.editMembershipTitle)
            .text = membership.title
        dialog.findViewById<EditText>(R.id.editMembershipPrice)
            .setText(membership.price.toString())


        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()


        val btnCancel = dialog.findViewById<Button>(R.id.btnCancelScheduleDialog)
        val btnUpdate = dialog.findViewById<Button>(R.id.btnAddScheduleDialog)

        val autoCompleteDuration : AutoCompleteTextView = dialog.findViewById(R.id.auto_complete_membership_duration)
        autoCompleteDuration.setText(membership.duration)
        utilsSetUpSelectMembershipDurationDropdown(requireContext(),autoCompleteDuration) { duration ->
            selectedDuration = duration
        }

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnUpdate.setOnClickListener {
            alertDialog.dismiss()
        }

    }

}