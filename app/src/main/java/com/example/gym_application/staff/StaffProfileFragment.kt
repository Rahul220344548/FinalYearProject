package com.example.gym_application.staff

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.gym_application.MainActivity
import com.example.gym_application.R
import com.example.gym_application.utils.DialogUtils
import com.google.firebase.auth.FirebaseAuth


class StaffProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_staff_profile, container, false)

        val btnSignOut = view.findViewById<Button>(R.id.btnStaffSignOut)
        btnSignOut.setOnClickListener {
            DialogUtils.showConfirmationDialog(
                requireContext(),
                onConfirm = { signOut() },
                onCancel = {}
            )
        }

        return view
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


}