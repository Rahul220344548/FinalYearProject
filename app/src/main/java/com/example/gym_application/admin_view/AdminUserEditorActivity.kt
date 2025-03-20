package com.example.gym_application.admin_view

import FirebaseDatabaseHelper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.R
import com.example.gym_application.controller.UserFirebaseDatabaseHelper
import com.example.gym_application.utils.ValidationUserFields
import com.example.gym_application.utils.utilsSetUpSelectActivateUser
import com.example.gym_application.utils.utilsSetUpSelectUserGender
import com.example.gym_application.utils.utilsSetUpSelectUserRoles
import com.google.android.material.button.MaterialButton
import org.w3c.dom.Text

class AdminUserEditorActivity : AppCompatActivity() {

    private lateinit var editFirstName : EditText
    private lateinit var editLastName : EditText

    private lateinit var editDobDay : EditText
    private lateinit var editDobMonth : EditText
    private lateinit var editDobYear : EditText

    private lateinit var editPhoneNumber : EditText

    private lateinit var autoCompleteGenderTextView: AutoCompleteTextView
    private lateinit var autoCompleteRoleTextView : AutoCompleteTextView

    private lateinit var autoCompleteActivationTextView: AutoCompleteTextView
    private lateinit var activateBtn : Button

    private lateinit var txtMembershipStatus : TextView
    private lateinit var txtMembershipType: TextView
    private lateinit var txtPlanExpiration: TextView

    private lateinit var inName: String
    private lateinit var inLastName : String
    private lateinit var inDateOfBirth : String
    private lateinit var inPhoneNumber : String
    private lateinit var inGender : String
    private lateinit var inRole : String

    private var selectedGender: String = ""
    private var selectedRoles: String = ""
    private var selectedStatus: String = ""

    private val firebaseHelper = UserFirebaseDatabaseHelper()
    private val classFirebareHelper = FirebaseDatabaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_user_editor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        showMembershipContainer()
        showActivateButtonForInActiveUser()
        initalizeTextFields()

    }

    private fun initalizeTextFields() {

        inName = intent.getStringExtra("firstName")?:""
        editFirstName = findViewById(R.id.userEditTextFirstName)
        editFirstName.setText(inName)

        inLastName = intent.getStringExtra("lastName")?:""
        editLastName = findViewById(R.id.userEditTextLastName)
        editLastName.setText(inLastName)

        inDateOfBirth = intent.getStringExtra("dateOfBirth")?:""

        setDateOfBirthFields(inDateOfBirth)

        inPhoneNumber = intent.getStringExtra("phoneNumber")?:""
        editPhoneNumber = findViewById(R.id.userEditTextPhoneNumber)
        editPhoneNumber.setText(inPhoneNumber)

        setUpSelectGenderdropdown()
        setUpSelectRoledropdown()

    }

    fun onSaveUserBtn(view : View) {

        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(this, "User Update Failed: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = intent.getStringExtra("uid") ?: ""
        val newUserFirstName = editFirstName.text.toString().trim()
        val newUserLastName = editLastName.text.toString().trim()
        val formattedDateOfBirth = formatDateofBirth()
        val newUserPhoneNumber = editPhoneNumber.text.toString().trim()
        val newUserSelectedGender = autoCompleteGenderTextView.text.toString().trim()
        val newUserSelectedRole = autoCompleteRoleTextView.text.toString().trim()

        val userUpdate = mapOf(
            "firstName" to newUserFirstName,
            "lastName" to newUserLastName ,
            "dateOfBirth" to formattedDateOfBirth,
            "gender" to newUserSelectedGender,
            "phoneNumber" to newUserPhoneNumber,
            "role" to newUserSelectedRole,
        )

        firebaseHelper.adminUpdateUserInfo( userId, userUpdate ) { success ->
            if (success){
                Toast.makeText(this,"User updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }else {
                Toast.makeText(this, "Failed to update class", Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun onDeleteUserBtn(view : View) {
        showDeleteUserDialog()
    }

    private fun showDeleteUserDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.delete_class_dialog_box, null)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.cancelbtn)
        val btnConfirmDeletion = dialogView.findViewById<MaterialButton>(R.id.btnConfirmDelete)

        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirmDeletion.setOnClickListener {
            alertDialog.dismiss()
            deleteUserFromDatabase()
        }
    }

    private fun deleteUserFromDatabase() {
        val userId = intent.getStringExtra("uid") ?: ""

        firebaseHelper.softDeleteUser(userId) { success ->
            if (success) {
                Toast.makeText(this, "User successfully soft deleted", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to soft delete user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showMembershipContainer() {

        inRole = intent.getStringExtra("role")?:""

        if (inRole!="Member") {
            val membershipContainer = findViewById<View>(R.id.membershipStatusContainer)
            membershipContainer?.visibility = View.GONE
            return
        }

        txtMembershipStatus = findViewById(R.id.AdminMembershipStatus)
        txtMembershipType = findViewById(R.id.AdminMembershipTypeTitle)
        txtPlanExpiration = findViewById(R.id.AdminMembershipExpiresEndDate)

        val userId = intent.getStringExtra("uid") ?: ""

        // Fetches User Membership Status and Displays Status Card
        firebaseHelper.fetchUserMembershipInfo(userId) { membershipInfo ->
            when {
                membershipInfo == null -> {
                    txtPlanExpiration.text = "Membership Not Purchased"
                    txtMembershipType.visibility = View.GONE
                    txtMembershipStatus.visibility = View.GONE
                }
                membershipInfo.status == "active" -> {

                    txtMembershipStatus.text = membershipInfo.status.uppercase()
                    txtMembershipType.text = "${membershipInfo.title} - ${membershipInfo.duration} Plan"
                    txtPlanExpiration.text = membershipInfo.expirationDate
                    txtMembershipType.visibility = View.VISIBLE
                    txtPlanExpiration.visibility = View.VISIBLE
                }
                else -> {
                    txtPlanExpiration.text = "Inactive"
                    txtMembershipType.visibility = View.GONE
                    txtMembershipStatus.visibility = View.GONE
                }
            }
        }

    }

    private fun showActivateButtonForInActiveUser() {
        val userId = intent.getStringExtra("uid") ?: ""
        firebaseHelper.fetchUserStatus(userId) { success, status ->
            if (status == "inactive") {
                activateBtn = findViewById(R.id.btnActivateUser)
                activateBtn.visibility = View.VISIBLE
            }
        }
    }

    fun onCancelUserBtn(view: View) {
        finish()
    }

    private fun setDateOfBirthFields(dateOfBirth: String) {

        val dateParts = dateOfBirth.split("/")
        if (dateParts.size == 3) {

            editDobDay = findViewById<EditText>(R.id.userEditDay)
            editDobMonth = findViewById<EditText>(R.id.userEditMonth)
            editDobYear = findViewById<EditText>(R.id.userEditYear)

            editDobDay.setText(dateParts[0])
            editDobMonth.setText(dateParts[1])
            editDobYear.setText(dateParts[2])
        } else {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setUpSelectGenderdropdown() {
        autoCompleteGenderTextView = findViewById(R.id.select_user_gender)
        inGender = intent.getStringExtra("gender")?:""
        autoCompleteGenderTextView.setText(inGender)
        utilsSetUpSelectUserGender(this,autoCompleteGenderTextView) { gender ->
            selectedGender = gender
        }
    }

    private fun setUpSelectRoledropdown() {
        autoCompleteRoleTextView = findViewById(R.id.select_user_role)
        inRole = intent.getStringExtra("role")?:""
        autoCompleteRoleTextView.setText(inRole)
        utilsSetUpSelectUserRoles( this, autoCompleteRoleTextView) { role ->
            selectedRoles = role
        }
    }

    private fun setUpSelectUserActivationdropdown(dialogView: View) {
        autoCompleteActivationTextView = dialogView.findViewById(R.id.select_user_status_to_activate)
        utilsSetUpSelectActivateUser(this, autoCompleteActivationTextView) { status ->
            selectedStatus = status
        }
    }

    private fun validationFields() : String {

       return ValidationUserFields.validateUserFields(
           firstName = editFirstName.text.toString().trim(),
           lastName = editLastName.text.toString().trim(),
           inDay = editDobDay.text.toString().trim(),
           inMonth = editDobMonth.text.toString().trim(),
           inYear = editDobYear.text.toString().trim(),
           phoneNumber = editPhoneNumber.text.toString().trim(),
           selectedGender = autoCompleteGenderTextView.text.toString().trim(),
           selectedRole = autoCompleteRoleTextView.text.toString().trim()
       )


    }

    private fun formatDateofBirth() : String {
        val newUserDay = editDobDay.text.toString().trim()
        val newUserMonth = editDobMonth.text.toString().trim()
        val newUserYear = editDobYear.text.toString().trim()

        inDateOfBirth = String.format(
            "%s/%s/%s",
            newUserDay,
            newUserMonth,
            newUserYear
        )
        return inDateOfBirth
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun onActivateBtn( view :View) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.inactive_user_container, null)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()


        setUpSelectUserActivationdropdown(dialogView)

        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.btnUserStatusCancel)
        val btnConfirm = dialogView.findViewById<MaterialButton>(R.id.btnActivateUserConfirm)

        val autoCompleteActivationTextView = dialogView.findViewById<AutoCompleteTextView>(R.id.select_user_status_to_activate)


        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val selectedStatus = autoCompleteActivationTextView.text.toString().trim()

            if (selectedStatus.isEmpty()) {
                Toast.makeText(this, "Please select a status before confirming!", Toast.LENGTH_SHORT).show()
            } else {
                alertDialog.dismiss()
                updateUserActivationStatus(selectedStatus)
            }
        }

    }

    private fun updateUserActivationStatus(selectedStatus: String) {
        val userId = intent.getStringExtra("uid") ?: ""

        firebaseHelper.updateUserStatus(userId) { success ->
            if (success) {
                Toast.makeText(this, "User successfully activated", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to activate User!", Toast.LENGTH_SHORT).show()
            }
            }

    }



}