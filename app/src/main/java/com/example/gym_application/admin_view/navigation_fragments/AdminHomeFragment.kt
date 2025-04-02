package com.example.gym_application.admin_view.navigation_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.gym_application.R
import com.example.gym_application.controller.UserFirebaseDatabaseHelper


class AdminHomeFragment : Fragment() {

    private lateinit var activeMember : TextView
    private lateinit var staffs : TextView
    private lateinit var inactiveMember : TextView
    private lateinit var TotalMembers : TextView

    val userFirebaseHelper = UserFirebaseDatabaseHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_home, container, false)

        activeMember = view.findViewById(R.id.labelMemberTotal)
        staffs = view.findViewById(R.id.labelStaffTotal)
        inactiveMember =  view.findViewById(R.id.labelInactiveMemberTotal)
        TotalMembers = view.findViewById(R.id.labelAllMemberTotal)

        getTotalActiveMembers()

        return view
    }

    private fun getTotalActiveMembers() {

        userFirebaseHelper.getUserStatsForAdmin {
                                              activeCount, staffCount,
                                              inactiveCount, totalUsers ->
            activeMember.text = activeCount.toString()
            staffs.text = staffCount.toString()
            inactiveMember.text = inactiveCount.toString()
            TotalMembers.text = totalUsers.toString()
        }
    }

}