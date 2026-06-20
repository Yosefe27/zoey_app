package com.siresystems.zoey_gardens_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.siresystems.zoey_gardens_app.adapter.MenuPagerAdapter
import com.siresystems.zoey_gardens_app.utils.CartManager

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var txtWelcomeUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 👤 Welcome text
        txtWelcomeUser = findViewById(R.id.txtWelcomeUser)

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val name = prefs.getString("name", "Guest")

        txtWelcomeUser.text = "Welcome $name"

        // 📦 ViewPager setup
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        viewPager.adapter = MenuPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Food Menu"
                1 -> "Beverages"
                2 -> "Promos"
                else -> "Menu"
            }
        }.attach()
    }

    // ================= MENU =================

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.menu_logout -> {
                logoutUser()
                return true
            }

            R.id.menu_full_menu -> {

                val dialog = MenuDialogFragment()
                dialog.show(supportFragmentManager, "menuDialog")

                return true
            }

            R.id.menu_orderstatus -> {
                startActivity(Intent(this, OrderTrackingActivity::class.java))
                return true
            }

            R.id.tech_support -> {
                startActivity(Intent(this, TechSupportActivity::class.java))
                return true
            }

            R.id.menu_orders -> {
                startActivity(Intent(this, OrderHistory::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // ================= LOGOUT =================

    private fun logoutUser() {

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        prefs.edit().clear().apply()

        CartManager.cartItems.clear()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}