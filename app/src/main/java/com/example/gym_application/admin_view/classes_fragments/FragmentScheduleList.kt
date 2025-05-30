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
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminScheduleListAdapter
import com.example.gym_application.adapter.ScheduleFirebaseHelper
import com.example.gym_application.adapter.UserFirebaseDatabaseHelper
import com.example.gym_application.newModel.Instructor
import com.example.gym_application.utils.ClassBookingUtils
import com.example.gym_application.utils.ScheduleUtils.createScheduleFromClassTemplate
import com.example.gym_application.utils.ScheduleUtils.generateNewScheduleId
import com.example.gym_application.utils.ValidationClassScheduleFields
import com.example.gym_application.utils.formatDateUtils
import com.example.gym_application.viewmodel.ScheduleViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@RequiresApi(Build.VERSION_CODES.O)
class FragmentScheduleList : Fragment() {
    private lateinit var eventListener: ValueEventListener
    private lateinit var databaseRef: DatabaseReference



    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : AdminScheduleListAdapter

    private lateinit var btnAddNewSchedule : Button

    private lateinit var autoCompleteClassName : AutoCompleteTextView
    private lateinit var autoCompleteClassLocation : AutoCompleteTextView
    private lateinit var autoCompleteClassInstructor: AutoCompleteTextView

    private lateinit var autoCompleteStartTime: AutoCompleteTextView
    private lateinit var autoCompleteEndTime : AutoCompleteTextView

    private lateinit var txtNoSchedules : TextView

    private lateinit var startDate: AutoCompleteTextView

    private var selectedRoom: String = ""
    private var selectedInstructor: String = ""

    private val classTitleToIdMap = mutableMapOf<String, String>()

    private val scheduleFirebaseHelper = ScheduleFirebaseHelper()
    private val classFirebaseHelper = FirebaseDatabaseHelper()
    private val userFirebaseHelper = UserFirebaseDatabaseHelper()
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_schedule_list, container, false)

        txtNoSchedules =  view.findViewById<TextView>(R.id.txtNoScheduleToday)

        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        setupRecyclerView(view)
        fetchSchedulesList()

        btnAddNewSchedule = view.findViewById<Button>(R.id.btnAddNewSchedule)
        btnAddNewSchedule.setOnClickListener {
            onCreateNewScheduleBtn()
        }

        databaseRef = FirebaseDatabase.getInstance().getReference("schedulesInfo")

        return view
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.newAdminClassesListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AdminScheduleListAdapter(emptyList())
        recyclerView.adapter = adapter
    }

    private fun fetchSchedulesList() {
        val today = formatDateUtils.getTodayDate()
        viewModel.attachListenerForDate(today) { schedules, error ->
            if (schedules.isNotEmpty() && !error) {
                adapter.updateData(schedules)
            } else {
                txtNoSchedules.visibility = View.GONE
                adapter.updateData(emptyList())
            }
        }
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

        setUpAllDropDownFields(dialogView)

        btnCancelDialog.setOnClickListener {
            alertDialog.dismiss()
        }

        var selectedInstructorObj: Instructor? = null
        ClassBookingUtils.setUpSelectInstructordropdown(requireContext(),dialogView) { instructor  ->
            selectedInstructorObj = instructor
        }


        btnConfirm.setOnClickListener {

            if (selectedInstructorObj == null) {
                Toast.makeText(requireContext(), "Please select an instructor.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onAddNewScheduleToDatabase(alertDialog, selectedInstructorObj!!)
        }

    }

    private fun onAddNewScheduleToDatabase(alertDialog: AlertDialog, selectedInstructor: Instructor){

        val validationMessage = validationFields()

        if (validationMessage.isNotEmpty()) {
            Toast.makeText(context, "Schedule Creation failed: $validationMessage", Toast.LENGTH_SHORT).show()
            return
        }

        val instructorId = selectedInstructor.userId
        val instructorName = selectedInstructor.name

        val selectedClassTitle = autoCompleteClassName.text.toString().trim()
        val selectedClassId = classTitleToIdMap[selectedClassTitle]
        val selectedClassLocation = autoCompleteClassLocation.text.toString().trim()
        val selectedClassInstructor = autoCompleteClassInstructor.text.toString().trim()
        val classStartTime = autoCompleteStartTime.text.toString().trim()
        val classEndTime = autoCompleteEndTime.text.toString().trim()
        val classStartDate = startDate.text.toString().trim()

        if (selectedClassId.isNullOrEmpty()) {
            Toast.makeText(context, "Invalid class selected.", Toast.LENGTH_SHORT).show()
            return
        }

        scheduleFirebaseHelper.checkForDuplicateSchedules(selectedClassId, classStartDate, classStartTime) { isDuplicate ->
            if (isDuplicate) {
                Toast.makeText(context, "A class is already scheduled at this time!", Toast.LENGTH_SHORT).show()
                return@checkForDuplicateSchedules
            }

            classFirebaseHelper.fetchClassTemplateByClassId(selectedClassId) { classTemplate ->
                if (classTemplate == null){
                    Toast.makeText(context, "Failed to fetch class details.", Toast.LENGTH_SHORT).show()
                    return@fetchClassTemplateByClassId
                }
                val newScheduleId = generateNewScheduleId() ?: return@fetchClassTemplateByClassId
                val newSchedule = createScheduleFromClassTemplate(
                    classTemplate,
                    newScheduleId,
                    selectedClassLocation,
                    selectedClassInstructor,
                    classStartDate,
                    classStartTime,
                    classEndTime
                )

                scheduleFirebaseHelper.newCreateClassScheduleEntry(
                    schedule = newSchedule,
                    onSuccess = { actualScheduleId ->
                        userFirebaseHelper.createScheduleEntryToInstructor(
                            context = requireContext(),
                            instructorId = instructorId,
                            classStartDate = classStartDate,
                            scheduleId = actualScheduleId
                        )
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


    @RequiresApi(Build.VERSION_CODES.O)
    private fun validationFields():String{
        return ValidationClassScheduleFields.validationClassScheduleFields(
            selectedClassName = autoCompleteClassName.text.toString().trim(),
            selectedRoom = autoCompleteClassLocation.text.toString().trim(),
            selectedInstructor = autoCompleteClassInstructor.text.toString().trim(),
            startTime = autoCompleteStartTime.text.toString().trim(),
            endTime = autoCompleteEndTime.text.toString().trim(),
            startDate = startDate.text.toString().trim(),
        )
    }

    private fun setUpAllDropDownFields(dialogView: View) {
        autoCompleteClassName = dialogView.findViewById(R.id.select_class_for_schedule)
        autoCompleteClassLocation = dialogView.findViewById(R.id.auto_complete_schedule_room)
        autoCompleteClassInstructor = dialogView.findViewById(R.id.auto_complete_schedule_instructor)
        autoCompleteStartTime = dialogView.findViewById(R.id.auto_complete_startTime)
        autoCompleteEndTime = dialogView.findViewById<AutoCompleteTextView>(R.id.auto_complete_endTime)
        startDate = dialogView.findViewById(R.id.auto_complete_starDate)

        ClassBookingUtils.setUpSelectClassesOptionsDropdown(requireContext(),dialogView) { classMap ->
            classTitleToIdMap.clear()
            classTitleToIdMap.putAll(classMap)
        }
        ClassBookingUtils.setUpSelectRoomdropdown(requireContext(),dialogView) { selectedRoom -> }

        var selectedInstructorObj: Instructor? = null
        ClassBookingUtils.setUpSelectInstructordropdown(requireContext(),dialogView) { instructor  ->
            selectedInstructorObj = instructor
        }



        ClassBookingUtils.setUpStartTimeDropdown(requireContext(),dialogView)
        ClassBookingUtils.setUpEndTimeDropdown(requireContext(), dialogView)
        ClassBookingUtils.setUpStartDate(requireContext(), dialogView)
    }


}