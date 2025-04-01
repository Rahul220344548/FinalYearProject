package com.example.gym_application.admin_view.adapter


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.AdminMembershipInfoEditorActivity
import com.example.gym_application.model.MembershipPlans
import com.example.gym_application.newModel.NewMembershipPlan


class AdminMembershipListAdapter (
    private var membershipList : List<NewMembershipPlan>,
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

        holder.title.text = "${membership.title} PLAN"
        holder.duration.text = membership.duration
        holder.price.text = membership.price.toString()


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, AdminMembershipInfoEditorActivity::class.java).apply {
                putExtra("id",membership.id)
                putExtra("title",membership.title)
                putExtra("duration", membership.duration)
                putExtra("price", membership.price)
            }
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = membershipList.size

    fun updateData( newList: List<NewMembershipPlan>) {
        membershipList = newList
        notifyDataSetChanged()
    }



}