package com.siresystems.zoey_gardens_app

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class OrdersActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val client = OkHttpClient()

    private val ordersList = ArrayList<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        listView = findViewById(R.id.ordersList)

        loadOrders()

        listView.setOnItemClickListener { _, _, position, _ ->
            showOrderOptions(ordersList[position])
        }
    }

    // =========================
    // LOAD ORDERS
    // =========================
    private fun loadOrders() {

        val request = Request.Builder()
            .url("https://zoeygardens-001-site1.site4future.com/zoey_apis/admin_get_orders.php")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@OrdersActivity, "Failed to load orders", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                val json = JSONObject(body ?: "")
                val data = json.getJSONArray("data")

                ordersList.clear()

                for (i in 0 until data.length()) {
                    ordersList.add(data.getJSONObject(i))
                }

                runOnUiThread {
                    listView.adapter = OrdersAdapter(ordersList)
                }
            }
        })
    }

    // =========================
    // CUSTOM ADAPTER (WITH COLORS)
    // =========================
    inner class OrdersAdapter(private val data: ArrayList<JSONObject>) : BaseAdapter() {

        override fun getCount() = data.size
        override fun getItem(position: Int) = data[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val view = convertView ?: LayoutInflater.from(this@OrdersActivity)
                .inflate(R.layout.order_row, parent, false)

            val order = data[position]

            val id = order.optString("id")
            val phone = order.optString("phone")
            val total = order.optString("total")
            val status = order.optString("status")

            val txtOrderId = view.findViewById<TextView>(R.id.txtOrderId)
            val txtPhone = view.findViewById<TextView>(R.id.txtPhone)
            val txtTotal = view.findViewById<TextView>(R.id.txtTotal)
            val txtStatus = view.findViewById<TextView>(R.id.txtStatus)

            txtOrderId.text = "Order #$id"
            txtPhone.text = "Phone: $phone"
            txtTotal.text = "Total: K$total"
            txtStatus.text = "Status: $status"

            // =========================
            // STATUS COLORS
            // =========================
            when (status.lowercase()) {

                "pending" -> txtStatus.setTextColor(Color.RED)

                "pending_payment" -> txtStatus.setTextColor(Color.parseColor("#FF9800"))

                "processing_order" -> txtStatus.setTextColor(Color.parseColor("#2196F3"))

                "completed" -> txtStatus.setTextColor(Color.parseColor("#4CAF50"))

                "completed_sent" -> txtStatus.setTextColor(Color.parseColor("#2E7D32"))

                else -> txtStatus.setTextColor(Color.GRAY)
            }

            return view
        }
    }

    // =========================
    // ORDER ACTIONS
    // =========================
    private fun showOrderOptions(order: JSONObject) {

        val id = order.optString("id")
        val phone = order.optString("phone")
        val itemsRaw = order.opt("items")

        val itemText = StringBuilder()

        try {
            if (itemsRaw is JSONArray) {

                for (i in 0 until itemsRaw.length()) {
                    val item = itemsRaw.getJSONObject(i)

                    itemText.append("• ")
                        .append(item.optString("name"))
                        .append(" x")
                        .append(item.optString("qty"))
                        .append(" (K")
                        .append(item.optString("price"))
                        .append(")\n")
                }

            } else {
                itemText.append(itemsRaw?.toString() ?: "No items")
            }
        } catch (e: Exception) {
            itemText.append(itemsRaw?.toString() ?: "No items")
        }

        val options = arrayOf(
            "View Order Details",
            "Send Payment Reminder",
            "Confirm Payment (Processing)",
            "Mark Completed",
            "Mark Sent to Customer"
        )

        AlertDialog.Builder(this)
            .setTitle("Order #$id")
            .setItems(options) { _, which ->

                when (which) {

                    0 -> AlertDialog.Builder(this)
                        .setTitle("Order #$id Details")
                        .setMessage("Phone: $phone\n\nItems:\n$itemText")
                        .setPositiveButton("OK", null)
                        .show()

                    1 -> {
                        sendWhatsApp(phone,
                            "Your order #$id has been received. Please ensure payment is made so we can process it."
                        )
                        updateStatus(id, "pending_payment")
                    }

                    2 -> {
                        sendWhatsApp(phone,
                            "Payment for order #$id received. Your order is now being processed."
                        )
                        updateStatus(id, "processing_order")
                    }

                    3 -> updateStatus(id, "completed")

                    4 -> {
                        sendWhatsApp(phone,
                            "Your order #$id has been completed and sent to delivery."
                        )
                        updateStatus(id, "completed_sent")
                    }
                }
            }
            .show()
    }

    // =========================
    // UPDATE STATUS
    // =========================
    private fun updateStatus(id: String, status: String) {

        val formBody = FormBody.Builder()
            .add("id", id)
            .add("status", status)
            .build()

        val request = Request.Builder()
            .url("https://zoeygardens-001-site1.site4future.com/zoey_apis/admin_update_order_status.php")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    Toast.makeText(this@OrdersActivity, "Updated", Toast.LENGTH_SHORT).show()
                    loadOrders()
                }
            }
        })
    }

    // =========================
    // WHATSAPP
    // =========================
    private fun sendWhatsApp(phone: String, message: String) {

        val url = "https://wa.me/$phone?text=${Uri.encode(message)}"

        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}