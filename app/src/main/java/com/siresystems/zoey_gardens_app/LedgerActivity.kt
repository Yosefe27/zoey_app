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

class LedgerActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private val client = OkHttpClient()

    private val ledgerList = ArrayList<JSONObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ledger)

        listView = findViewById(R.id.ledgerList)

        loadLedger()
    }

    private fun loadLedger() {

        val request = Request.Builder()
            .url("https://zoeygardens-001-site1.site4future.com/zoey_apis/get_ledger.php")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LedgerActivity, "Failed to load ledger", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()
                val json = JSONObject(body ?: "")
                val data = json.getJSONArray("data")

                ledgerList.clear()

                for (i in 0 until data.length()) {
                    ledgerList.add(data.getJSONObject(i))
                }

                runOnUiThread {
                    listView.adapter = LedgerAdapter(ledgerList)
                }
            }
        })
    }

    inner class LedgerAdapter(private val data: ArrayList<JSONObject>) : BaseAdapter() {

        override fun getCount() = data.size
        override fun getItem(position: Int) = data[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val view = convertView ?: layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false)

            val item = data[position]

            val date = item.getString("date")
            val income = item.getDouble("income")
            val expense = item.getDouble("expense_total")
            val balance = item.getDouble("running_balance")

            val text1 = view.findViewById<TextView>(android.R.id.text1)
            val text2 = view.findViewById<TextView>(android.R.id.text2)

            text1.text = "📅 $date | Income: K$income | Expense: K$expense"

            text2.text = "💰 Running Balance: K$balance"

            // COLOR RULES
            when {
                balance > 0 -> text2.setTextColor(Color.parseColor("#2E7D32"))
                balance < 0 -> text2.setTextColor(Color.RED)
                else -> text2.setTextColor(Color.GRAY)
            }

            return view
        }
    }
}