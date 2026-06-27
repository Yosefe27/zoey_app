package com.siresystems.zoey_gardens_app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.siresystems.zoey_gardens_app.api.ApiClient
import com.siresystems.zoey_gardens_app.model.DailySale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.DatePickerDialog
import java.text.SimpleDateFormat

import com.siresystems.zoey_gardens_app.model.SaveSale
import com.siresystems.zoey_gardens_app.model.SaveSalesRequest
import com.siresystems.zoey_gardens_app.model.GenericResponse

import java.util.*

class DailySalesActivity :
    AppCompatActivity(),
    TotalListener {

    private lateinit var txtWaiter: TextView
    private lateinit var txtGrandTotal: TextView
    private lateinit var edtDate: EditText
    private lateinit var edtSearch: EditText
    private lateinit var recycler: RecyclerView
    private lateinit var btnSave: Button

    private lateinit var adapter: SalesAdapter

    private val allItems =
        mutableListOf<DailySale>()

    private val displayedItems =
        mutableListOf<DailySale>()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_daily_sales
        )

        txtWaiter =
            findViewById(R.id.txtWaiter)

        txtGrandTotal =
            findViewById(R.id.txtGrandTotal)

        edtDate =
            findViewById(R.id.edtDate)

        edtSearch =
            findViewById(R.id.edtSearch)

        recycler =
            findViewById(R.id.recyclerSales)

        btnSave =
            findViewById(R.id.btnSave)

        val prefs =
            getSharedPreferences(
                "user",
                MODE_PRIVATE
            )

        val waiter =
            prefs.getString(
                "name",
                ""
            ) ?: ""

        txtWaiter.text =
            "Waiter: $waiter"

        edtDate.isFocusable = false
        edtDate.isClickable = true

        edtDate.setOnClickListener {

            val cal = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->

                    val selected =
                        String.format(
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            day
                        )

                    edtDate.setText(selected)

                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

/*
        edtDate.setText(
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())
        )

 */

        adapter =
            SalesAdapter(
                displayedItems,
                this
            )

        recycler.layoutManager =
            LinearLayoutManager(this)

        recycler.adapter =
            adapter

        loadItems()

        edtSearch.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {

                    val search =
                        s.toString()
                            .lowercase()

                    displayedItems.clear()

                    displayedItems.addAll(
                        allItems.filter {
                            it.item_description
                                .lowercase()
                                .contains(search)
                        }
                    )

                    adapter.notifyDataSetChanged()
                }
            })

        btnSave.setOnClickListener {

            val entryDate =
                edtDate.text.toString().trim()

            if (entryDate.isEmpty()) {

                edtDate.error =
                    "Select business date"

                return@setOnClickListener
            }

            val prefs =
                getSharedPreferences(
                    "user",
                    MODE_PRIVATE
                )

            val waiter =
                prefs.getString(
                    "name",
                    ""
                ) ?: ""

            val sales =
                allItems
                    .filter {
                        it.quantity > 0
                    }
                    .map {

                        SaveSale(
                            item_description =
                                it.item_description,

                            item_price =
                                it.item_price,

                            quantity =
                                it.quantity
                        )
                    }

            if (sales.isEmpty()) {

                Toast.makeText(
                    this,
                    "No sales entered",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text = "Saving..."

            val request =
                SaveSalesRequest(
                    entry_date = entryDate,
                    employee = waiter,
                    sales = sales
                )

            ApiClient.instance
                .saveDailySales(request)
                .enqueue(

                    object :
                        Callback<GenericResponse> {

                        override fun onResponse(
                            call: Call<GenericResponse>,
                            response: Response<GenericResponse>
                        ) {

                            btnSave.isEnabled = true
                            btnSave.text =
                                "SAVE SALES"

                            if (
                                response.isSuccessful &&
                                response.body()?.status ==
                                "success"
                            ) {

                                Toast.makeText(
                                    this@DailySalesActivity,
                                    "Sales saved successfully",
                                    Toast.LENGTH_LONG
                                ).show()

                                allItems.forEach {
                                    it.quantity = 0
                                }

                                adapter.notifyDataSetChanged()

                                onTotalChanged()

                                // 🔥 RETURN TO DASHBOARD
                                finish()

                            } else {

                                Toast.makeText(
                                    this@DailySalesActivity,
                                    "Failed to save",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<GenericResponse>,
                            t: Throwable
                        ) {

                            btnSave.isEnabled = true
                            btnSave.text =
                                "SAVE SALES"

                            Toast.makeText(
                                this@DailySalesActivity,
                                t.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
        }
    }

    private fun loadItems() {

        ApiClient.instance
            .getItems()
            .enqueue(
                object :
                    Callback<List<DailySale>> {

                    override fun onResponse(
                        call: Call<List<DailySale>>,
                        response: Response<List<DailySale>>
                    ) {

                        if (response.isSuccessful) {

                            val items = response.body()?.map {
                                it.copy(quantity = 0) // 🔥 RESET HERE
                            } ?: emptyList()

                            allItems.clear()
                            allItems.addAll(items)

                            displayedItems.clear()
                            displayedItems.addAll(items)

                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onFailure(
                        call: Call<List<DailySale>>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@DailySalesActivity,
                            t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
    }

    override fun onTotalChanged() {

        val total =
            allItems.sumOf {
                it.total()
            }

        txtGrandTotal.text =
            "TOTAL: K%.2f"
                .format(total)
    }
}