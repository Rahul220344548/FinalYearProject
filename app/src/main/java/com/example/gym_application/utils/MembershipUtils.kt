package com.example.gym_application.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.gym_application.R
import com.example.gym_application.model.MembershipPlans

object MembershipUtils {

}

//    fun createEditMembershipDialog(context: Context, membership: MembershipPlans, onDurationSelected: (String) -> Unit): AlertDialog {
//        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_membership_plan, null)
//
//        dialogView.findViewById<TextView>(R.id.editMembershipTitle).text = membership.title
//        dialogView.findViewById<EditText>(R.id.editMembershipPrice).setText(membership.price.toString())
//
//        val autoCompleteDuration: AutoCompleteTextView = dialogView.findViewById(R.id.auto_complete_membership_duration)
//        autoCompleteDuration.setText(membership.duration)
//        setUpSelectMembershipDurationDropdown(context, autoCompleteDuration, onDurationSelected)
//
//        val dialogBuilder = AlertDialog.Builder(context)
//            .setView(dialogView)
//            .setCancelable(false)
//
//        return dialogBuilder.create().apply {
//            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        }
//    }
//
//
//
//
//    fun configureDialogButtons(dialog: AlertDialog, onCancel: () -> Unit, onUpdate: () -> Unit) {
//        val dialogView = dialog.findViewById<View>(R.layout.dialog_edit_membership_plan) ?: return
//        dialogView.findViewById<Button>(R.id.btnCancelScheduleDialog).setOnClickListener {
//            onCancel()
//            dialog.dismiss()
//        }
//        dialogView.findViewById<Button>(R.id.btnAddScheduleDialog).setOnClickListener {
//            onUpdate()
//            dialog.dismiss()
//        }
//    }
//
//    fun updateMembershipDetails() {
//
//
