package com.example.gym_application.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.controller.FAQAdapter.ViewHolder
import com.example.gym_application.model.FAQ
import com.example.gym_application.utils.DialogUtils

class UserFAQAdapter (
    private var questionLists : List<FAQ>
):
    RecyclerView.Adapter<UserFAQAdapter.ViewHolder>(){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val title : TextView = view.findViewById(R.id.faqTitle)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faq_plan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = questionLists[position]
        holder.title.text = list.question

        holder.itemView.setOnClickListener {
            DialogUtils.showUserViewFAQDialog(
                holder.itemView.context,
                list.question,
                list.answer
            )
        }

    }

    override fun getItemCount(): Int = questionLists.size

    fun updateData(newList: List<FAQ>) {
        questionLists = newList
        notifyDataSetChanged()
    }

}