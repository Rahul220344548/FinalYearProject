package com.example.gym_application.utils

import android.content.Context
import android.content.Intent
import android.view.View

import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import com.example.gym_application.R
import com.example.gym_application.model.Membership
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object MembershipUtils {

    fun setUpSelectMembershipDurationsdropdown(
        context: Context,
        dialogView: View,
        onSelectedDuration: (String) -> Unit
    ) {
        val autoCompleteDurationTextView = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_membership_duration)
        utilsSetUpSelectMembershipDurationDropdown(context,autoCompleteDurationTextView,onSelectedDuration )
    }

    fun createMembershipFromIntent(intent: Intent): Membership {
        val membershipAmount = intent.getIntExtra("planPrice", 0)
        val membershipDuration = intent.getStringExtra("planDuration") ?: "1 Month"
        val membershipTitle = intent.getStringExtra("planTitle") ?: "Default Plan Title"
        val startDate = calculateMembershipStartDate()
        val endDate = calculateMembershipEndDate(membershipDuration)

        return createMembershipDetailsTemplate(
            membershipTitle = membershipTitle,
            planPrice = membershipAmount,
            membershipDuration = membershipDuration,
            date = startDate,
            startDate = startDate,
            endDate = endDate
        )
    }


    fun createMembershipDetailsTemplate(
        membershipTitle : String,
        planPrice : Int,
        membershipDuration: String,
        date : String,
        startDate : String,
        endDate : String
    ): Membership {

       return  Membership (
        membershipTitle = membershipTitle,
           planPrice = planPrice,
           membershipStatus = "active",
           membershipDuration = membershipDuration,
           date = date,
           startDate = startDate,
           endDate = endDate
       )
    }

    fun calculateMembershipStartDate(): String{
        val calendar = Calendar.getInstance()
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
    }

    fun calculateMembershipEndDate( duration : String): String {
        val numberOfMonths = duration.split(" ")[0].toIntOrNull() ?: 0
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, numberOfMonths)
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
    }

}

