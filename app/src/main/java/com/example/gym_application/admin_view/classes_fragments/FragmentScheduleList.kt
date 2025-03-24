package com.example.gym_application.admin_view.classes_fragments

import FirebaseDatabaseHelper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.gym_application.R
import com.example.gym_application.controller.ScheduleFirebaseHelper
import com.example.gym_application.model.Schedule
import com.example.gym_application.utils.ClassBookingUtils
import com.example.gym_application.utils.ValidationClassScheduleFields
import com.google.android.material.button.MaterialButton


class FragmentScheduleList : Fragment() {


    private lateinit var btnAddNewSchedule : Button

    private lateinit var autoCompleteClassName : AutoCompleteTextView
    private lateinit var autoCompleteStartTime: AutoCompleteTextView
    private lateinit var autoCompleteEndTime : AutoCompleteTextView

    private lateinit var startDate: AutoCompleteTextView

    private var selectedClasses : String = ""

    private val classTitleToIdMap = mutableMapOf<String, String>()

    private val scheduleFirebaseHelper = ScheduleFirebaseHelper()

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

        /*
        Setup dropdowns
         */
        autoCompleteClassName = dialogView.findViewById(R.id.select_class_for_schedule)
        autoCompleteStartTime = dialogView.findViewById(R.id.auto_complete_startTime)
        autoCompleteEndTime = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_endTime)
        startDate = dialogView.findViewById(R.id.auto_complete_starDate)

        ClassBookingUtils.setUpSelectClassesOptionsDropdown(requireContext(),dialogView,) { classMap ->
            classTitleToIdMap.clear()
            classTitleToIdMap.putAll(classMap)
        }

        ClassBookingUtils.setUpStartTimeDropdown(requireContext(),dialogView)
        ClassBookingUtils.setUpEndTimeDropdown(requireContext(), dialogView)
        ClassBookingUtils.setUpStartDate(requireContext(), dialogView)

        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            onAddNewScheduleToDatabase(alertDialog)
        }

    }

    private fun onAddNewScheduleToDatabase(alertDialog: AlertDialog){

        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(context, "Schedule Creation failed: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedClassTitle = autoCompleteClassName.text.toString()
        val selectedClassId = classTitleToIdMap[selectedClassTitle]

        if (selectedClassId.isNullOrEmpty()) {
            Toast.makeText(context, "Invalid class selected.", Toast.LENGTH_SHORT).show()
            return
        }


        val classStarTime = autoCompleteStartTime.text.toString().trim()
        val classEndTime = autoCompleteEndTime.text.toString().trim()
        val classStartDate = startDate.text.toString().trim()

        val newSchedule = Schedule(
            classId = selectedClassId,
            classStartTime = classStarTime,
            classEndTime = classEndTime,
            classStartDate = classStartDate,
            classCurrentBookings = 0,
            status = "active"
        )

        scheduleFirebaseHelper.checkForDuplicateSchedules(selectedClassId, classStartDate, classStarTime) { isDuplicate ->
            if (isDuplicate) {
                Toast.makeText(context, "A class is already scheduled at this time!", Toast.LENGTH_SHORT).show()
            }else {
                scheduleFirebaseHelper.createClassScheduleEntry(
                    schedule = newSchedule,
                    onSuccess = {
                        Toast.makeText(context, "Schedule created successfully!", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                                },
                    onFailure = { exception ->
                        Toast.makeText(context, "Failed to create schedule: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )

            }
        }

    }
    // Duplication check
    // Do Checks if schedule already exists at a specfic time
    @RequiresApi(Build.VERSION_CODES.O)
    private fun validationFields():String{
        return ValidationClassScheduleFields.validationClassScheduleFields(
            selectedClassName = autoCompleteClassName.text.toString().trim(),
            startTime = autoCompleteStartTime.text.toString().trim(),
            endTime = autoCompleteEndTime.text.toString().trim(),
            startDate = startDate.text.toString().trim(),
        )
    }

}