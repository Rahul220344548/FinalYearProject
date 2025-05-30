package com.example.gym_application.admin_view.classes_fragments

import FirebaseDatabaseHelper
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gym_application.R
import com.example.gym_application.admin_view.adapter.AdminClassListAdapter
import com.example.gym_application.model.ClassTemplate


class FragmentClassList : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : AdminClassListAdapter

    private lateinit var searchView : SearchView

    private val classFirebaseHelper = FirebaseDatabaseHelper()
    private var classList : List<ClassTemplate> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_class_list, container, false)

        recyclerView = view.findViewById(R.id.adminClassesListRecyclerView)
        searchView = view.findViewById(R.id.findClasseSearchView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = AdminClassListAdapter(emptyList())
        recyclerView.adapter = adapter

        fetchClassesList()


        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        return view

    }

    private fun fetchClassesList() {
        classFirebaseHelper.listenForClassUpdates { fetchedList  ->
            if (fetchedList.isNotEmpty()) {
                classList = fetchedList
                adapter.updateData(classList)
            }else {
                println("Error fetching classes")
            }
        }
    }

    private fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            adapter.updateData(classList)
        }else {
            val filteredList = classList.filter { it.classTitle.contains(query, ignoreCase = true) }
            adapter.updateData(filteredList)
        }
    }
}