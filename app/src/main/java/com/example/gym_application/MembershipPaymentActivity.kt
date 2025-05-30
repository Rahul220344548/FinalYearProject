package com.example.gym_application

import android.content.Intent
import android.os.Bundle
import android.os.WorkDuration
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.model.Membership
import com.example.gym_application.model.MembershipPlans
import com.example.gym_application.utils.MembershipUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import helper.FirebaseMemebershipHelper
import org.w3c.dom.Text
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MembershipPaymentActivity : AppCompatActivity() {

    private lateinit var txtMembershipTypeTitle: TextView
    private lateinit var txtMembershipDuration: TextView
    private lateinit var txtMembershipAmount: TextView
    private lateinit var txtMembershipStartDate: TextView
    private lateinit var txtMembershipEndDate: TextView

    private val firebaseMembershipHelper = FirebaseMemebershipHelper()
    private val userId = getCurrentUserId().toString()

    private val PAYPAL_REQUEST_CODE = 123
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership_payment)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContentView(R.layout.activity_membership_payment)

        val toolbar: Toolbar = findViewById(R.id.user_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        val amount = intent.getIntExtra("planPrice",0)

        setConfirmationCardDetails()
        // Set up PayPal configuration and start the service
        val config = createPayPalConfig()
        startPayPalService(config)
        //  Set up button to start PayPal payment process
        setupPaymentButton(config, amount)
    }

    private fun setConfirmationCardDetails( ) {

        val planTitle = intent.getStringExtra( "planTitle") ?: "Default Plan Title"
        val capitalizePlanTitle =  planTitle.uppercase()
        val planDuration = intent.getStringExtra( "planDuration") ?: "Default Plan Title"
        val planAmount = intent.getIntExtra("planPrice",0)

        txtMembershipTypeTitle = findViewById<TextView>(R.id.getMembershipTitle)
        txtMembershipDuration = findViewById<TextView>(R.id.getMembershipDuration)
        txtMembershipAmount = findViewById<TextView>(R.id.getTotalPayment)
        txtMembershipStartDate = findViewById<TextView>(R.id.getStartDate)
        txtMembershipEndDate = findViewById<TextView>(R.id.getEndDate)

        txtMembershipTypeTitle.text = "$capitalizePlanTitle"
        setMembershipTypeTitleColors(planTitle)
        txtMembershipTypeTitle.visibility = View.VISIBLE


        txtMembershipDuration.text = "$planDuration"
        txtMembershipDuration.visibility = View.VISIBLE

        txtMembershipStartDate.text = MembershipUtils.calculateMembershipStartDate()
        txtMembershipStartDate.visibility = View.VISIBLE

        txtMembershipEndDate.text = MembershipUtils.calculateMembershipEndDate( planDuration )
        txtMembershipEndDate.visibility = View.VISIBLE

        val formattedAmount = String.format("%.2f", planAmount.toDouble())
        txtMembershipAmount.text = "£$formattedAmount"
        txtMembershipAmount.visibility = View.VISIBLE


    }

    private fun createPayPalConfig() : PayPalConfiguration {
        return PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId("ARRtTwZBxuikFG6eTd6dsHCnPNmKw_qz8X15fpIDSrMIewQUfGBlCa3js6XWGPZvZ4IHEcp2GaJk330D")
    }

    private fun startPayPalService(config: PayPalConfiguration) {
        val intent = Intent(this, PayPalService::class.java).apply {
            putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        }
        startService(intent)
    }
    // When the pay button is clicked, this launches the PayPal payment screen
    private fun setupPaymentButton(config: PayPalConfiguration, amount: Int) {
        val button = findViewById<Button>(R.id.btnPay)
        button.setOnClickListener {
            val payment = createPayPalPayment(amount)
            val intent = Intent(this, PaymentActivity::class.java).apply {
                putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
                putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
            }
            startActivityForResult(intent, PAYPAL_REQUEST_CODE)
        }
    }
    // Creates a PayPal payment object with amount and currency
    private fun createPayPalPayment(amount: Int): PayPalPayment {
        return PayPalPayment(
            BigDecimal.valueOf(amount.toDouble()), "GBP", "Gym Memberrship",
            PayPalPayment.PAYMENT_INTENT_SALE
        )
    }
    // Handles the result of the PayPal payment
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {

                    val membership = MembershipUtils.createMembershipFromIntent(intent)
                    // Save membership to Firebase if user is logged in
                    if (userId != null) {
                        firebaseMembershipHelper.writeMembershipDetailsToUser( userId, membership, onSuccess = {
                            Toast.makeText(this, "Payment Successful! Membership Activated.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        },
                            onFailure = {e ->
                                Toast.makeText(this, "Something went wrong!${e.message}", Toast.LENGTH_LONG).show()
                            })
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Payment is canceled", Toast.LENGTH_SHORT).show()

            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Payment is invalid", Toast.LENGTH_SHORT).show()

            }
        }
    }


    private fun setMembershipTypeTitleColors(planTitle: String) {

        when (planTitle) {
            "Bronze" -> txtMembershipTypeTitle.setTextColor(ContextCompat.getColor(this, R.color.bronze))
            "Silver" -> txtMembershipTypeTitle.setTextColor(ContextCompat.getColor(this, R.color.silver))
            "Gold" -> txtMembershipTypeTitle.setTextColor(ContextCompat.getColor(this, R.color.gold))
            else -> txtMembershipTypeTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

    }

    private fun getCurrentUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
