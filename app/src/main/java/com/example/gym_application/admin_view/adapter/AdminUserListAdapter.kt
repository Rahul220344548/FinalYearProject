package com.example.gym_application.admin_view.adapter

import android.content.Intent
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.AdminUserEditorActivity
import com.example.gym_application.model.UserDetails

class AdminUserListAdapter (
    private  var userList : List<UserDetails>,
    private val userIdMap: MutableMap<UserDetails, String>
) :

    RecyclerView.Adapter<AdminUserListAdapter.ViewHolder>() {

    class ViewHolder ( view: View) : RecyclerView.ViewHolder(view) {

        val userName: TextView = view.findViewById(R.id.userFullName)
        val userRole: TextView = view.findViewById(R.id.userRole)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_users_list,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        val userId = userIdMap[user] ?: ""
        holder.userName.text = "${user.firstName} ${user.lastName}"
        holder.userRole.text = user .role

        holder.itemView.setOnClickListener {
            val intent =
                Intent(holder.itemView.context, AdminUserEditorActivity::class.java).apply {
                    putExtra("uid", userId)
                    putExtra("firstName", user.firstName)
                    putExtra("lastName", user.lastName)
                    putExtra("dateOfBirth", user.dateOfBirth)
                    putExtra("gender", user.gender)
                    putExtra("phoneNumber", user.phoneNumber)
                    putExtra("role", user.role)
                }
            holder.itemView.context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = userList.size

    fun updateData(newList: List<UserDetails>, newUserIdMap: Map<UserDetails, String>) {
        userList = newList
        userIdMap.clear()
        userIdMap.putAll(newUserIdMap)
        notifyDataSetChanged()
    }

}
