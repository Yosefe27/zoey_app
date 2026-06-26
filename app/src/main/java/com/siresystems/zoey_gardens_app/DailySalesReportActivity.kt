package com.siresystems.zoey_gardens_app

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class DailySalesReportActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var fromDate: EditText
    private lateinit var toDate: EditText
    private lateinit var btnFilter: Button

    private val client = OkHttpClient()

    private val displayList = ArrayList<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_report)

        listView = findViewById(R.id.salesList)
        fromDate = findViewById(R.id.fromDate)
        toDate = findViewById(R.id.toDate)
        btnFilter = findViewById(R.id.btnFilter)

        loadSales(null, null)

        btnFilter.setOnClickListener {
            loadSales(fromDate.text.toString(), toDate.text.toString())
        }
    }

    private fun loadSales(from: String?, to: String?) {

        var url = "https://zoeygardens-001-site1.site4future.com/zoey_apis/get_daily_sales_report.php"

        if (!from.isNullOrEmpty() && !to.isNullOrEmpty()) {
            url += "?from=$from&to=$to"
        }

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@DailySalesReportActivity, "Failed to load", Toast.LENGTH_SHORT).show()
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
                    listView.adapter = SalesAdapter(displayList)
                }
            }
        })
    }

    inner class SalesAdapter(private val data: ArrayList<JSONObject>) : BaseAdapter() {

        override fun getCount() = data.size
        override fun getItem(position: Int) = data[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val view = convertView ?: layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false)

            val item = data[position]

            val date = item.getString("date")
            val totals = item.getJSONObject("totals")

            val text1 = view.findViewById<TextView>(android.R.id.text1)
            val text2 = view.findViewById<TextView>(android.R.id.text2)

            text1.text = "📅 $date | Qty: ${totals.getInt("qty")}"
            text2.text = "💰 Value: K${totals.getDouble("value")}"

            when {
                totals.getDouble("value") > 0 -> text2.setTextColor(Color.parseColor("#2E7D32"))
                else -> text2.setTextColor(Color.GRAY)
            }

            return view
        }
    }
}