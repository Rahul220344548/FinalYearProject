package com.example.gym_application.admin_view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.R
import com.example.gym_application.utils.DialogUtils
import com.example.gym_application.utils.FaqUtils
import helper.FirebaseFAQHelper


class AdminFaqEditorActivity : AppCompatActivity() {

    private lateinit var editFaqTitle : EditText
    private lateinit var editFaqAnswer : EditText

    private lateinit var inId : String
    private lateinit var inQuestion : String
    private lateinit var inAnswer : String

    private val firebaseFaqHelper = FirebaseFAQHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_faq_editor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        inId = intent.getStringExtra( "id") ?: ""

        initalizeTextFields()

        val btnUpdateFaq : Button = findViewById(R.id.btnAddFaqDialog)
        btnUpdateFaq.setOnClickListener {
            DialogUtils.showConfirmationDialog(
                this,
                onConfirm = { updateFAQDetails() },
                onCancel = {}
            )
        }

        val btnDeleteFaq : Button = findViewById(R.id.btnDeleteFaq)
        btnDeleteFaq.setOnClickListener {
            DialogUtils.showConfirmDeleteDialog(
                this,
                onConfirm = { dialogView, alertDialog ->
                    deleteFaqFromDatabase()
                },
                onCancel = {}
            )
        }

        val btncancelMembershipDialog: Button = findViewById(R.id.btnCancelFaqDialog)
        btncancelMembershipDialog.setOnClickListener {
            finish()
        }

    }

    private fun initalizeTextFields() {
        inQuestion = intent.getStringExtra("question")?:""
        inAnswer = intent.getStringExtra("answer")?:""

        editFaqTitle = findViewById(R.id.editFaqQuestion)
        editFaqAnswer = findViewById(R.id.editFaqAnswer)

        editFaqTitle.setText(inQuestion)
        editFaqAnswer.setText(inAnswer)
    }

    private fun updateFAQDetails() {

        val question = editFaqTitle.text.toString().trim()
        val answer = editFaqAnswer.text.toString().trim()

        val validationMessage = FaqUtils.validationFields(question,answer)

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(this, "Error: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        val faqUpdate = mapOf(
            "question" to question,
            "answer" to answer,
        )

        firebaseFaqHelper.updateFAQInfo(inId, faqUpdate) { success ->
            if (success) {
                Toast.makeText(this, "FAQ updated sucessfully!", Toast.LENGTH_SHORT).show()
                finish()
            }else {
                Toast.makeText(this, "Failed to update FAQ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteFaqFromDatabase() {
        firebaseFaqHelper.deleteFAQFromDatabase( inId, onSuccess = {
            Toast.makeText(this, "FAQ deleted successfully!", Toast.LENGTH_SHORT).show()
            finish()
        },
            onFailure = { e ->
                Toast.makeText(this, "Failed to delete FAQ: ${e.message}", Toast.LENGTH_LONG).show()
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



}