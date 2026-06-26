package com.siresystems.zoey_gardens_app

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class InventroyReportActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var searchBtn: Button
    private lateinit var searchInput: EditText

    private val client = OkHttpClient()

    private val displayList = ArrayList<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_report)

        listView = findViewById(R.id.inventoryList)
        searchBtn = findViewById(R.id.btnSearch)
        searchInput = findViewById(R.id.itemSearch)

        loadInventory(null)

        searchBtn.setOnClickListener {
            val item = searchInput.text.toString()
            loadInventory(item)
        }
    }

    private fun loadInventory(item: String?) {

        var url = "https://zoeygardens-001-site1.site4future.com/zoey_apis/get_inventory_report.php"

        if (!item.isNullOrEmpty()) {
            url += "?item_search=$item"
        }

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@InventroyReportActivity, "Failed to load inventory", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                val json = JSONObject(body ?: "")
                val data = json.getJSONArray("data")

                displayList.clear()

                for (i in 0 until data.length()) {
                    displayList.add(data.getJSONObject(i))
                }

                runOnUiThread {
                    listView.adapter = InventoryAdapter(displayList)
                }
            }
        })
    }

    inner class InventoryAdapter(private val data: ArrayList<JSONObject>) : BaseAdapter() {

        override fun getCount() = data.size
        override fun getItem(position: Int) = data[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val view = convertView ?: layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false)

            val period = data[position]
            val items = period.getJSONArray("items")

            val totals = period.getJSONObject("totals")

            val title = view.findViewById<TextView>(android.R.id.text1)
            val subtitle = view.findViewById<TextView>(android.R.id.text2)

            val periodId = period.getString("period_id")
            val status = period.getString("status")

            title.text = "📦 Period $periodId | $status"

            subtitle.text =
                "Sales: K${totals.getDouble("sales_value")} | " +
                        "Shortage: K${totals.getDouble("shortage_value")}"

            when (status.lowercase()) {
                "open" -> title.setTextColor(Color.BLUE)
                "closed" -> title.setTextColor(Color.RED)
                else -> title.setTextColor(Color.DKGRAY)
            }

            return view
        }
    }
}