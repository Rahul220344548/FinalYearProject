package com.example.gym_application.utils

import com.example.gym_application.model.MembershipPlans

interface OnMembershipClickListener {
    fun onMembershipClicked(membership: MembershipPlans)
}