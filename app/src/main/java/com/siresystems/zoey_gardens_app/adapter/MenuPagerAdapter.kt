package com.siresystems.zoey_gardens_app.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.siresystems.zoey_gardens_app.ui.MenuFragment

class MenuPagerAdapter(activity: AppCompatActivity) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MenuFragment.newInstance("Food")
            1 -> MenuFragment.newInstance("Beverages")
            2 -> MenuFragment.newInstance("Promos")   // ✅ NEW TAB
            else -> MenuFragment.newInstance("Food")
        }
    }
}