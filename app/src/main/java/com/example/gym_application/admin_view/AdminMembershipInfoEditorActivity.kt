package com.example.gym_application.admin_view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.R
import com.example.gym_application.utils.DialogUtils
import com.example.gym_application.utils.utilsSetUpSelectMembershipDurationDropdown
import com.google.android.material.button.MaterialButton

class AdminMembershipInfoEditorActivity : AppCompatActivity() {

    private lateinit var editPlanTitle : EditText
    private lateinit var autoCompleteDurationTextView: AutoCompleteTextView
    private lateinit var editPlanPrice : EditText

    private lateinit var inPlanTitle : String
    private lateinit var inPlanDuration : String
    private var inPlanPrice : Int = 0
    private var selectedDurations: String = ""


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

        val btnUpdateMembership: Button = findViewById(R.id.btnAddScheduleDialog)
        btnUpdateMembership.setOnClickListener {
            DialogUtils.showConfirmationDialog(
                this,
                onConfirm = { updateMembershipDetails() },
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

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}