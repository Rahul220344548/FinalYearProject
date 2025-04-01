package com.example.gym_application.admin_view.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.model.MembershipPlans
import com.example.gym_application.utils.OnMembershipClickListener

class AdminMembershipListAdapter (
    private val context: Context,
    private var membershipList : List<MembershipPlans>,
    private val listener: OnMembershipClickListener
) :
    RecyclerView.Adapter<AdminMembershipListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.planTitle)
        val duration: TextView = view.findViewById(R.id.planDuration)
        val price: TextView = view.findViewById(R.id.planPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membership_plan, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val membership = membershipList[position]
        holder.title.text = membership.title
        holder.duration.text = membership.duration
        holder.price.text = membership.price.toString()

        holder.itemView.setOnClickListener {
            listener.onMembershipClicked(membership)
        }


    }


    override fun getItemCount(): Int = membershipList.size

    fun updateData( newList: List<MembershipPlans>) {
        membershipList = newList
        notifyDataSetChanged()
    }



}