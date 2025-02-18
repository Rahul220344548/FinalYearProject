package com.example.gym_application.admin_view

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.R
import com.example.gym_application.model.ClassModel
import com.google.firebase.database.FirebaseDatabase

class CreateAClass : AppCompatActivity() {

    private lateinit var classTitle: EditText
    private lateinit var classDescription: EditText

    private val database = FirebaseDatabase.getInstance().getReference("classes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_aclass)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

        classTitle =  findViewById<EditText>(R.id.editTextClassTitle)
        classDescription = findViewById<EditText>(R.id.editTextClassDescription)


    }

    fun addClassbtn(view: View) {
        val title = classTitle.text.toString().trim()
        val description = classDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this,"Please fill in blacks",Toast.LENGTH_SHORT).show()
            return
        }


        val classId = database.push().key
        if (classId != null) {
            val newClass = ClassModel(title, description)

            database.child(classId).setValue(newClass)
                .addOnSuccessListener {
                    Toast.makeText(this, "Added Class Successfully", Toast.LENGTH_SHORT).show()
                    classTitle.text.clear()
                    classDescription.text.clear()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error adding class: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }


        Toast.makeText(this,"Added Class Successfully",Toast.LENGTH_SHORT).show()

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun dropdown() {

//        val rootView = inflater.inflate(R.layout.fragment_admin_classes, container, false)
//
//        val colors = resources.getStringArray(R.array.colors)
//
//        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, colors)
//
//        val autoCompleteTextView = rootView.findViewById<AutoCompleteTextView>(R.id.auto_complete_txt)
//
//        autoCompleteTextView.setAdapter(arrayAdapter)
//
//        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
//            val selectedColor = parent.getItemAtPosition(position) as String
//            // Do something with the selected color
//        }
//
//        return rootView
    }

    fun onCancelbtn(view: View){
        finish()
    }

}