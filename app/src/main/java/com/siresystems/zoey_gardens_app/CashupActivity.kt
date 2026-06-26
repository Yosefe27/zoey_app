package com.siresystems.zoey_gardens_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CashupActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var filterBtn: Button
    private lateinit var dateInput: EditText

    private val client = OkHttpClient()

    private val displayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashups)

        listView = findViewById(R.id.cashupList)
        filterBtn = findViewById(R.id.btnFilter)
        dateInput = findViewById(R.id.dateFilter)

        loadCashups(null)

        filterBtn.setOnClickListener {
            val date = dateInput.text.toString()
            loadCashups(date)
        }
    }

    // =========================
    // LOAD CASHUPS
    // =========================
    private fun loadCashups(date: String?) {

        var url = "https://zoeygardens-001-site1.site4future.com/zoey_apis/get_cashups.php"

        if (!date.isNullOrEmpty()) {
            url += "?date=$date"
        }

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CashupActivity, "Failed to load", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {

                val body = response.body?.string()
                val json = JSONObject(body ?: "")
                val data = json.getJSONObject("data")

                displayList.clear()

                val dates = data.keys()

                while (dates.hasNext()) {

                    val date = dates.next()
                    val array = data.getJSONArray(date)

                    displayList.add("===== $date =====")

                    var dVisa = 0.0
                    var dCash = 0.0
                    var dMomo = 0.0
                    var dExpected = 0.0
                    var dCashed = 0.0
                    var dShortage = 0.0

                    for (i in 0 until array.length()) {

                        val obj = array.getJSONObject(i)

                        val visa = obj.getDouble("VisaAmount")
                        val cash = obj.getDouble("CashAmount")
                        val momo = obj.getDouble("MomoAmount")
                        val expected = obj.getDouble("Expected")
                        val cashed = obj.getDouble("CashedTotal")
                        val shortage = obj.getDouble("Shortage")

                        dVisa += visa
                        dCash += cash
                        dMomo += momo
                        dExpected += expected
                        dCashed += cashed
                        dShortage += shortage

                        displayList.add(
                            "${obj.getString("EmployeeName")}\n" +
                                    "Visa: K$visa Cash: K$cash Momo: K$momo\n" +
                                    "Expected: K$expected | Actual: K$cashed | Short: K$shortage"
                        )
                    }

                    displayList.add(
                        "--- DAILY TOTAL ---\n" +
                                "Visa: K$dVisa Cash: K$dCash Momo: K$dMomo\n" +
                                "Expected: K$dExpected Actual: K$dCashed Short: K$dShortage"
                    )
                }

                runOnUiThread {
                    listView.adapter = ArrayAdapter(
                        this@CashupActivity,
                        android.R.layout.simple_list_item_1,
                        displayList
                    )
                }
            }
        })
    }
}