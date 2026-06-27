package com.siresystems.zoey_gardens_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WaiterDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerDashboard: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiter_dashboard)

        // Set title
        supportActionBar?.title = "Waiter Dashboard"

        recyclerDashboard = findViewById(R.id.recyclerDashboard)

        // Dashboard items
        val dashboardItems = listOf(

            DashboardItem(
                "Capture Daily Sales",
                R.drawable.sales
            ),

            DashboardItem(
                "My Daily Sales Report",
                R.drawable.app_sales
            )
        )

        // 3 columns
        recyclerDashboard.layoutManager =
            GridLayoutManager(this, 3)

        // Adapter
        recyclerDashboard.adapter =
            DashboardAdapter(dashboardItems) { item ->

                when (item.title) {
                    "Capture Daily Sales" -> {
                        startActivity(
                            Intent(
                                this,
                                DailySalesActivity::class.java
                            )
                        )
                    }

                    "My Daily Sales Report" -> {
                        startActivity(
                            Intent(
                                this,
                                SalesHistoryActivity::class.java
                            )
                        )
                    }
                }
            }
    }

    // Create logout button in toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(
            R.menu.admin_menu,
            menu
        )

        return true
    }

    // Logout click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_logout) {

            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes") { _, _ ->

                    val prefs =
                        getSharedPreferences(
                            "user",
                            MODE_PRIVATE
                        )

                    prefs.edit().clear().apply()

                    startActivity(
                        Intent(
                            this,
                            WaiterLoginActivity::class.java
                        )
                    )

                    finish()
                }
                .setNegativeButton("No", null)
                .show()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    // Handle phone back button
    override fun onBackPressed() {

        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Do you want to logout?")
            .setPositiveButton("Yes") { _, _ ->

                val prefs =
                    getSharedPreferences(
                        "user",
                        MODE_PRIVATE
                    )

                prefs.edit().clear().apply()

                startActivity(
                    Intent(
                        this,
                        WaiterLoginActivity::class.java
                    )
                )

                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

}