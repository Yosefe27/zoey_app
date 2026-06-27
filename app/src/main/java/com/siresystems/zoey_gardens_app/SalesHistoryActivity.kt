package com.siresystems.zoey_gardens_app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.siresystems.zoey_gardens_app.adapter.SalesHistoryAdapter
import com.siresystems.zoey_gardens_app.api.ApiClient
import com.siresystems.zoey_gardens_app.model.SalesHistory
import com.siresystems.zoey_gardens_app.model.SalesGroup
import com.siresystems.zoey_gardens_app.model.SalesHistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class SalesHistoryActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var edtDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_history)

        recycler = findViewById(R.id.recyclerSalesHistory)
        edtDate = findViewById(R.id.edtDateFilter)

        recycler.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val waiter = prefs.getString("name", "") ?: ""

        loadSales(waiter, null)

        edtDate.setOnClickListener {
            val cal = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, y, m, d ->
                    val date = String.format("%04d-%02d-%02d", y, m + 1, d)
                    edtDate.setText(date)
                    loadSales(waiter, date)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun loadSales(employee: String, date: String?) {

        ApiClient.instance.getWaiterSales(employee, date)
            .enqueue(object : Callback<SalesHistoryResponse> {

                override fun onResponse(
                    call: Call<SalesHistoryResponse>,
                    response: Response<SalesHistoryResponse>
                ) {

                    if (response.isSuccessful) {

                        val data = response.body()?.data ?: emptyList()

                        val grouped = groupSales(data)

                        recycler.adapter = SalesHistoryAdapter(grouped)
                    }
                }

                override fun onFailure(call: Call<SalesHistoryResponse>, t: Throwable) {
                    Toast.makeText(
                        this@SalesHistoryActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // 🔥 FIXED: NOW PROPERLY INSIDE THE CLASS
    private fun groupSales(list: List<SalesHistory>): List<SalesGroup> {

        return list
            .groupBy { it.entry_date }
            .map { entry ->

                val items = entry.value
                val total = items.sumOf { it.value }

                SalesGroup(
                    date = entry.key,
                    total = total,
                    items = items
                )
            }
            .sortedByDescending { it.date }
    }
}