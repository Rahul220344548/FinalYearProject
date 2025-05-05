package com.example.gym_application.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.AdminFaqEditorActivity
import com.example.gym_application.model.FAQ



class FAQAdapter (
    private var questionLists : List<FAQ>
):
    RecyclerView.Adapter<FAQAdapter.ViewHolder>() {

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
            val intent = Intent(holder.itemView.context, AdminFaqEditorActivity::class.java).apply {
                putExtra("id",list.id)
                putExtra("question",list.question)
                putExtra("answer", list.answer)
            }
            holder.itemView.context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int = questionLists.size

    fun updateData(newList: List<FAQ>) {
        questionLists = newList
        notifyDataSetChanged()
    }

}