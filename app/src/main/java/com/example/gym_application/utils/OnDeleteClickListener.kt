package com.example.gym_application.utils

import com.example.gym_application.model.UserDetails

interface OnDeleteClickListener {
    fun onDeleteClick(user: UserDetails)
}