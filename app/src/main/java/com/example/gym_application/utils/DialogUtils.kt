package com.example.gym_application.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.gym_application.R
import com.google.android.material.button.MaterialButton

object DialogUtils {
    fun showConfirmationDialog(context: Context, onConfirm: () -> Unit, onCancel: () -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_update, null)

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.btnCancelDialogBox)
        val btnConfirmDeletion = dialogView.findViewById<MaterialButton>(R.id.btnConfirmUpdate)

        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
            onCancel()
        }

        btnConfirmDeletion.setOnClickListener {
            alertDialog.dismiss()
            onConfirm()
        }
    }

    fun showMembershipDialog(
        context: Context,
        onConfirm: (View, AlertDialog) -> Unit,
        onCancel: () -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_membership_plan, null)

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        MembershipUtils.setUpSelectMembershipDurationsdropdown(context,dialogView) { selectedDuration ->}

        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.btnCancelScheduleDialog)
        val btnConfirm = dialogView.findViewById<MaterialButton>(R.id.btnAddScheduleDialog)


        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
            onCancel()
        }

        btnConfirm.setOnClickListener {
            onConfirm(dialogView,alertDialog)
        }
    }

    fun showConfirmDeleteDialog(
        context: Context,
        onConfirm: (View, AlertDialog) -> Unit,
        onCancel: () -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.delete_class_dialog_box, null)

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.cancelbtn)
        val btnConfirm = dialogView.findViewById<MaterialButton>(R.id.btnConfirmDelete)


        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
            onCancel()
        }

        btnConfirm.setOnClickListener {
            onConfirm(dialogView,alertDialog)
        }
    }

    fun showFAQDialog(
        context: Context,
        onConfirm: (View, AlertDialog) -> Unit,
        onCancel: () -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_faq, null)

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()


        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.btnCancelFaqDialog)
        val btnConfirm = dialogView.findViewById<MaterialButton>(R.id.btnAddFaqDialog)


        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
            onCancel()
        }

        btnConfirm.setOnClickListener {
            onConfirm(dialogView,alertDialog)
        }
    }

    fun showUserViewFAQDialog(context: Context, question: String, answer: String){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_view_faq, null)

        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val txtQuestion = dialogView.findViewById<TextView>(R.id.txtViewFaqQuestion)
        val txtAnswer = dialogView.findViewById<TextView>(R.id.txtViewFaqAnswer)
        txtQuestion.text = question
        txtAnswer.text = answer

        val btnCloseDialog = dialogView.findViewById<MaterialButton>(R.id.btnCloseFaqDialog)

        btnCloseDialog.setOnClickListener {
            alertDialog.dismiss()

        }


    }

    fun showToast(context: Context, message: String, isLong: Boolean = false) {
        val duration = if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        Toast.makeText(context, message, duration).show()
    }


}
