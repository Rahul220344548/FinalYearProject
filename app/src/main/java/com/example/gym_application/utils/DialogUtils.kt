package com.example.gym_application.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
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
}
