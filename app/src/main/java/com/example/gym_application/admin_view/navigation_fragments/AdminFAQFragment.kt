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
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.gym_application.R
import com.example.gym_application.controller.FAQAdapter
import com.example.gym_application.model.FAQ
import com.example.gym_application.utils.DialogUtils
import com.example.gym_application.utils.ValidationClassCreation
import com.example.gym_application.utils.ValidationMembershipCreationFields
import com.google.firebase.database.FirebaseDatabase
import helper.FirebaseFAQHelper
import helper.FirebaseMemebershipHelper


class AdminFAQFragment : Fragment() {

    private lateinit var editFaqTitle : EditText
    private lateinit var editFaqAnswer : EditText


    private lateinit var recyclerView: View
    private lateinit var adapter: FAQAdapter
    private val database = FirebaseDatabase.getInstance().getReference("faq")

    private val firebaseFaqHelper = FirebaseFAQHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_admin_payment, container, false)

//        recyclerView = view.findViewById(R.id.adminFaqListRecyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        adapter = FAQAdapter(emptyList())
//        recyclerView.adapter = adapter
//
        fetchFAQList()

        var goToCreateFAQBtn : Button = view.findViewById(R.id.btnAddNewFAQ)
        goToCreateFAQBtn.setOnClickListener { showFAQDialog() }

        return view
    }

    private fun fetchFAQList() {
    }

    fun showFAQDialog() {
        DialogUtils.showFAQDialog(
            requireContext(),
            onConfirm = { dialogView, alertDialog  ->
                storeFAQToDataBase(dialogView,alertDialog)
            },
            onCancel = {}
        )
    }

    private fun storeFAQToDataBase(dialogView: View, alertDialog: AlertDialog) {
        editFaqTitle = dialogView.findViewById(R.id.editFaqQuestion)
        editFaqAnswer = dialogView.findViewById(R.id.editFaqAnswer)

        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(context, "Error: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        val id = database.push().key ?: return
        val title = editFaqTitle.text.toString().trim()
        val answer = editFaqAnswer.text.toString().trim()

        val newFAQTemplate = FAQ(id,title,answer)


        firebaseFaqHelper.createFAQEntry(id,newFAQTemplate,
            onSuccess = {
                Toast.makeText(context, "FAQ created successfully!", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            },
            onFailure = { exception ->
                Toast.makeText(
                    context,
                    "Failed to create FAQ: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )


    }

    private fun validationFields(): String {
        val question = editFaqTitle.text.toString().trim()
        val answer = editFaqAnswer.text.toString().trim()

        if (!ValidationClassCreation.isValidClassDescription(question)) {
            return "FAQ Question must be between 1 and 120 words"
        }
        if (!ValidationClassCreation.isValidClassDescription(answer)){
            return "FAQ Answer must be between 1 and 120 words"
        }
        return ""
    }

}