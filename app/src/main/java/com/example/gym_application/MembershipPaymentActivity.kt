package com.example.gym_application

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gym_application.model.MembershipPlans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import java.math.BigDecimal

class MembershipPaymentActivity : AppCompatActivity() {
    private val PAYPAL_REQUEST_CODE = 123
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membership_payment)
        val amount = intent.getIntExtra("planPrice",0)

        val config = PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId("ARRtTwZBxuikFG6eTd6dsHCnPNmKw_qz8X15fpIDSrMIewQUfGBlCa3js6XWGPZvZ4IHEcp2GaJk330D")

        val intent = Intent(this, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        startService(intent)

        val button = findViewById<Button>(R.id.btnPay)

        button.setOnClickListener {
            val payment = PayPalPayment(
                BigDecimal.valueOf(amount.toDouble()), "GBP", "Pitch Booking",
                PayPalPayment.PAYMENT_INTENT_SALE)
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
            startActivityForResult(intent, PAYPAL_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirm != null) {
                    auth = FirebaseAuth.getInstance()
                    val uId = auth.currentUser?.uid
                    database.child(uId.toString()).child("add your membership object with properties")


                    val intent = Intent(this, HomeActivity::class.java)

                    Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Payment is canceled", Toast.LENGTH_SHORT).show()

            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Payment is invalid", Toast.LENGTH_SHORT).show()

            }
        }
    }
}
