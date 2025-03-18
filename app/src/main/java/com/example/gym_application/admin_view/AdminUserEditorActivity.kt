package com.example.gym_application.admin_view

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gym_application.R
import org.w3c.dom.Text

class AdminUserEditorActivity : AppCompatActivity() {

    private lateinit var txtTitle : TextView
    private lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_user_editor)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "GymEase"

//        name = intent.getStringExtra("firstName")?:""
//        txtTitle = findViewById(R.id.HELLOWORLD)
//        txtTitle.setText(name)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}