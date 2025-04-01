package com.example.gym_application.utils

import android.content.Context
import android.view.View

import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import com.example.gym_application.R


object MembershipUtils {

    fun setUpSelectMembershipDurationsdropdown(
        context: Context,
        dialogView: View,
        onSelectedDuration: (String) -> Unit
    ) {
        val autoCompleteDurationTextView = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_membership_duration)
        utilsSetUpSelectMembershipDurationDropdown(context,autoCompleteDurationTextView,onSelectedDuration )
    }


}

