package com.example.gym_application.admin_view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.R
import com.example.gym_application.utils.DialogUtils
import com.example.gym_application.utils.ValidationMembershipCreationFields
import com.example.gym_application.utils.utilsSetUpSelectMembershipDurationDropdown
import com.google.android.material.button.MaterialButton
import helper.FirebaseMemebershipHelper

class AdminMembershipInfoEditorActivity : AppCompatActivity() {

    private lateinit var editPlanTitle : EditText
    private lateinit var autoCompleteDurationTextView: AutoCompleteTextView
    private lateinit var editPlanPrice : EditText

    private lateinit var inPlanId : String
    private lateinit var inPlanTitle : String
    private lateinit var inPlanDuration : String
    private var inPlanPrice : Int = 0
    private var selectedDurations: String = ""
    private var firebaseMemebershipHelper = FirebaseMemebershipHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_membership_info_editor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        inPlanId = intent.getStringExtra("id") ?: ""

        val btnUpdateMembership: Button = findViewById(R.id.btnAddScheduleDialog)
        btnUpdateMembership.setOnClickListener {
            DialogUtils.showConfirmationDialog(
                this,
                onConfirm = { updateMembershipDetails() },
                onCancel = {}
            )
        }

        val btnDeleteMemberrship :  Button = findViewById(R.id.deleteMembershipPlan)
        btnDeleteMemberrship.setOnClickListener {
            DialogUtils.showConfirmDeleteDialog(
                this,
                onConfirm = { dialogView, alertDialog ->
                    deleteMembershipPlan(alertDialog)
                },
                onCancel = {}
            )
        }

        val btncancelMembershipDialog: Button = findViewById(R.id.btnCancelScheduleDialog)
        btncancelMembershipDialog.setOnClickListener {
            finish()
        }

        initalizeTextFields()

    }

    private fun initalizeTextFields(){

        inPlanTitle = intent.getStringExtra("title")?:""
        editPlanTitle = findViewById(R.id.editMembershipTitle)
        editPlanTitle.setText(inPlanTitle)

        autoCompleteDurationTextView = findViewById(R.id.auto_complete_membership_duration)
        inPlanDuration = intent.getStringExtra("duration")?:""
        autoCompleteDurationTextView.setText(inPlanDuration)
        utilsSetUpSelectMembershipDurationDropdown ( this, autoCompleteDurationTextView) { duration ->
            selectedDurations = duration
        }

        inPlanPrice =  intent.getIntExtra("price",0)
        editPlanPrice = findViewById(R.id.editMembershipPrice)
        editPlanPrice.setText(inPlanPrice.toString())
    }

    private fun updateMembershipDetails() {

        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(this, "Error: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        val title = editPlanTitle.text.toString().trim().capitalize()
        val duration = autoCompleteDurationTextView.text.toString().trim()
        val price = editPlanPrice.text.toString().trim().toIntOrNull() ?: 0

        val planUpdate = mapOf (
            "title" to title,
            "duration" to duration,
            "price" to price,
        )

        firebaseMemebershipHelper.updateMembershipInfo(inPlanId, planUpdate) { success ->
            if (success) {
                Toast.makeText(this, "Membership Plan updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update Membership", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun deleteMembershipPlan(alertDialog: AlertDialog) {
        firebaseMemebershipHelper.deleteMembershipFromDatabase( inPlanId, onSuccess = {
            Toast.makeText(this, "Membership deleted successfully", Toast.LENGTH_SHORT).show()
            finish()
        },
            onFailure = { e ->
                Toast.makeText(this, "Failed to delete membership: ${e.message}", Toast.LENGTH_LONG).show()
            })
    }

    private fun validationFields(): String {
        return ValidationMembershipCreationFields.validationMembershipCreationFields(
            title = editPlanTitle.text.toString().trim(),
            seletedDuration = autoCompleteDurationTextView.text.toString().trim(),
            price = editPlanPrice.text.toString().trim()
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}