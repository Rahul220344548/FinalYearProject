package com.example.gym_application.admin_view.classes_fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.gym_application.R
import com.google.android.material.button.MaterialButton


class FragmentScheduleList : Fragment() {


    private lateinit var btnAddNewSchedule : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_schedule_list, container, false)

        btnAddNewSchedule = view.findViewById<Button>(R.id.btnAddNewSchedule)

        btnAddNewSchedule.setOnClickListener {
            onCreateNewScheduleBtn()
        }

        return view
    }


    fun onCreateNewScheduleBtn() {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_schedule, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val btnCancelDialog = dialogView.findViewById<MaterialButton>(R.id.btnCancelScheduleDialog)
        val btnConfirm = dialogView.findViewById<MaterialButton>(R.id.btnAddScheduleDialog)



        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            alertDialog.dismiss()

        }

    }

}