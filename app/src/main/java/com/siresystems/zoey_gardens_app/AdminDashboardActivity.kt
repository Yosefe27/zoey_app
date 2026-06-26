package com.siresystems.zoey_gardens_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerDashboard: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Set title
        supportActionBar?.title = "Admin Dashboard"

        recyclerDashboard = findViewById(R.id.recyclerDashboard)

        // Dashboard items
        val dashboardItems = listOf(

            DashboardItem(
                "Manage Orders",
                R.drawable.orders
            ),

            DashboardItem(
                "Cash Up Report",
                R.drawable.money
            ),

            DashboardItem(
                "Daily Sales Report",
                R.drawable.sales
            ),

            DashboardItem(
                "Expenses Report",
                R.drawable.expes
            ),

            DashboardItem(
                "Inventory Report",
                R.drawable.inventory
            ),

            DashboardItem(
                "Other Reports",
                R.drawable.reports
            )
        )

        // 3 columns
        recyclerDashboard.layoutManager =
            GridLayoutManager(this, 3)

        // Adapter
        recyclerDashboard.adapter =
            DashboardAdapter(dashboardItems) { item ->

                when (item.title) {

                    "Manage Orders" -> {
                        startActivity(
                            Intent(
                                this,
                                OrdersActivity::class.java
                            )
                        )
                    }

                    "Cash Up Report" -> {
                        startActivity(
                            Intent(
                                this,
                                CashupActivity::class.java
                            )
                        )
                    }

                    "Daily Sales Report" -> {
                        startActivity(
                            Intent(
                                this,
                                DailySalesReportActivity::class.java
                            )
                        )
                    }

                    "Expenses Report" -> {
                        startActivity(
                            Intent(
                                this,
                                LedgerActivity::class.java
                            )
                        )
                    }

                    "Inventory Report" -> {
                        startActivity(
                            Intent(
                                this,
                                InventroyReportActivity::class.java
                            )
                        )
                    }
                    "Other Reports" -> {
                        startActivity(
                            Intent(
                                this,
                                FinancialDashboardActivity::class.java
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
                            AdminLoginActivity::class.java
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
                        AdminLoginActivity::class.java
                    )
                )

                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

}