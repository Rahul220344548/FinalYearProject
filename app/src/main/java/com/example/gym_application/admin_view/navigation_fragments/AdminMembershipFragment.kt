package com.example.gym_application.admin_view.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminMembershipListAdapter
import com.example.gym_application.newModel.NewMembershipPlan
import com.example.gym_application.utils.DialogUtils
import com.example.gym_application.utils.MembershipUtils
import com.example.gym_application.utils.ValidationMembershipCreationFields
import com.google.firebase.database.FirebaseDatabase
import helper.FirebaseMemebershipHelper


class AdminMembershipFragment : Fragment() {

    private lateinit var editPlanTitle : EditText
    private lateinit var autoCompleteDurationTextView: AutoCompleteTextView
    private lateinit var editPlanPrice : EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminMembershipListAdapter

    private var selectedDuration: String = ""

    private val database = FirebaseDatabase.getInstance().getReference("memberships")
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

        var goToCreateMembershipBtn :Button = view.findViewById(R.id.btnAddNewMembership)
        goToCreateMembershipBtn.setOnClickListener { btnAddNewMembershipPlan() }

        return view
    }


    private fun fetchMembershipList() {
        firebaseMembershipHelper.fetchAllMembershipPlansAsList { plans ->
            adapter.updateData(plans)
        }
    }

    fun btnAddNewMembershipPlan() {
        DialogUtils.showMembershipDialog(
            requireContext(),
            onConfirm = { dialogView ->
                setUpAllFields(dialogView)
                storeMembershipPlanToDatabase(dialogView)
            },
            onCancel = {}
        )
    }

    private fun storeMembershipPlanToDatabase(dialogView: View) {

        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(context, "Error: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        val membershipId = database.push().key ?: return
        val title = editPlanTitle.text.toString().trim().capitalize()
        val duration = autoCompleteDurationTextView.text.toString().trim()
        val price = editPlanPrice.text.toString().trim().toIntOrNull() ?: 0

        val newMembershipPlan = NewMembershipPlan(membershipId,title, duration, price)

        firebaseMembershipHelper.createMembershipEntry(membershipId, newMembershipPlan,
            onSuccess = {
                Toast.makeText(context, "Class created successfully!", Toast.LENGTH_SHORT).show()
            },
            onFailure = { exception ->
                Toast.makeText(
                    context,
                    "Failed to create membership plan: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }


    private fun validationFields(): String {
        return ValidationMembershipCreationFields.validationMembershipCreationFields(
            title = editPlanTitle.text.toString().trim(),
            seletedDuration = autoCompleteDurationTextView.text.toString().trim(),
            price = editPlanPrice.text.toString().trim()
        )
    }

    private fun setUpAllFields(dialogView: View) {
        editPlanTitle = dialogView.findViewById(R.id.editMembershipTitle)
        autoCompleteDurationTextView = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_membership_duration)
        editPlanPrice = dialogView.findViewById(R.id.editMembershipPrice)
    }


}