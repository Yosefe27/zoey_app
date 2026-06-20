package com.siresystems.zoey_gardens_app

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.siresystems.zoey_gardens_app.adapter.OrdersAdapter
import com.siresystems.zoey_gardens_app.model.Order

class OrderHistory : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var btnRefresh: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_tracking)

        recycler = findViewById(R.id.recyclerOrders)
        btnRefresh = findViewById(R.id.btnRefresh)

        recycler.layoutManager = LinearLayoutManager(this)

        loadOrders()

        btnRefresh.setOnClickListener {
            loadOrders()
        }
    }

    private fun loadOrders() {

        // ✅ GET PHONE FROM SHARED PREFERENCES
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val phone = prefs.getString("phone", "") ?: ""

        if (phone.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ DYNAMIC URL WITH PHONE
        val url =
            "https://zoeygardens-001-site1.site4future.com/zoey_apis/get_orderhistory.php?phone=$phone"

        val queue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->

                val list = ArrayList<Order>()

                for (i in 0 until response.length()) {

                    val obj = response.getJSONObject(i)

                    list.add(
                        Order(
                            id = obj.getInt("id"),
                            total = obj.getDouble("total"),
                            created_at = obj.getString("created_at"),
                            status = obj.getString("status")
                        )
                    )
                }

                recycler.adapter = OrdersAdapter(list)
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Failed to load orders", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(request)
    }
}