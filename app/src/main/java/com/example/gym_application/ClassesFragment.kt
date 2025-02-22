package com.example.gym_application

import ClassesPagerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ClassesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_classes, container, false)

        val viewPager: ViewPager2 = rootView.findViewById(R.id.viewPager)
        val adapter = ClassesPagerAdapter(this)
        viewPager.adapter = adapter

        val tabLayout: TabLayout = rootView.findViewById(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Gym Classes"
                1 -> tab.text = "Booked Classes"
            }
        }.attach()

        return rootView

    }


}