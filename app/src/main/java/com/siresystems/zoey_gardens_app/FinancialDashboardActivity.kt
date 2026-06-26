package com.siresystems.zoey_gardens_app

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class FinancialDashboardActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    // INCOME
    private lateinit var txtIncomeVisa: TextView
    private lateinit var txtIncomeCash: TextView
    private lateinit var txtIncomeMomo: TextView
    private lateinit var txtIncomeTotal: TextView

    // EXPENSES
    private lateinit var txtExpenseVisa: TextView
    private lateinit var txtExpenseCash: TextView
    private lateinit var txtExpenseMomo: TextView
    private lateinit var txtExpenseTotal: TextView

    // BALANCES
    private lateinit var txtBalanceVisa: TextView
    private lateinit var txtBalanceCash: TextView
    private lateinit var txtBalanceMomo: TextView

    // PROFIT
    private lateinit var txtProfitIncome: TextView
    private lateinit var txtProfitExpense: TextView
    private lateinit var txtProfit: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial_dashboard)

        // INCOME
        txtIncomeVisa = findViewById(R.id.txtIncomeVisa)
        txtIncomeCash = findViewById(R.id.txtIncomeCash)
        txtIncomeMomo = findViewById(R.id.txtIncomeMomo)
        txtIncomeTotal = findViewById(R.id.txtIncomeTotal)

        // EXPENSES
        txtExpenseVisa = findViewById(R.id.txtExpenseVisa)
        txtExpenseCash = findViewById(R.id.txtExpenseCash)
        txtExpenseMomo = findViewById(R.id.txtExpenseMomo)
        txtExpenseTotal = findViewById(R.id.txtExpenseTotal)

        // BALANCES
        txtBalanceVisa = findViewById(R.id.txtBalanceVisa)
        txtBalanceCash = findViewById(R.id.txtBalanceCash)
        txtBalanceMomo = findViewById(R.id.txtBalanceMomo)

        // PROFIT
        txtProfitIncome = findViewById(R.id.txtProfitIncome)
        txtProfitExpense = findViewById(R.id.txtProfitExpense)
        txtProfit = findViewById(R.id.txtProfit)

        loadDashboard()
    }

    private fun loadDashboard() {

        val request = Request.Builder()
            .url("https://zoeygardens-001-site1.site4future.com/zoey_apis/get_financial_dashboard.php")
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@FinancialDashboardActivity,
                        "Failed to load dashboard",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()

                if (body.isNullOrEmpty()) {
                    runOnUiThread {
                        Toast.makeText(
                            this@FinancialDashboardActivity,
                            "Empty response",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return
                }

                try {
                    val json = JSONObject(body)

                    val inc = json.getJSONObject("income")
                    val exp = json.getJSONObject("expenses")
                    val bal = json.getJSONObject("balances")
                    val prof = json.getJSONObject("profit")

                    runOnUiThread {

                        // ================= INCOME =================
                        txtIncomeVisa.text = "Visa: K${inc.getDouble("visa")}"
                        txtIncomeCash.text = "Cash: K${inc.getDouble("cash")}"
                        txtIncomeMomo.text = "Momo: K${inc.getDouble("momo")}"
                        txtIncomeTotal.text = "TOTAL: K${inc.getDouble("total")}"

                        // ================= EXPENSES =================
                        txtExpenseVisa.text = "Visa: K${exp.getDouble("visa")}"
                        txtExpenseCash.text = "Cash: K${exp.getDouble("cash")}"
                        txtExpenseMomo.text = "Momo: K${exp.getDouble("momo")}"
                        txtExpenseTotal.text = "TOTAL: K${exp.getDouble("total")}"

                        // ================= BALANCES =================
                        txtBalanceVisa.text = "Visa Balance: K${bal.getDouble("visa")}"
                        txtBalanceCash.text = "Cash Balance: K${bal.getDouble("cash")}"
                        txtBalanceMomo.text = "Momo Balance: K${bal.getDouble("momo")}"

                        // ================= PROFIT =================
                        txtProfitIncome.text = "Income: K${prof.getDouble("income")}"
                        txtProfitExpense.text = "Expenses: K${prof.getDouble("expense")}"
                        txtProfit.text = "Profit: K${prof.getDouble("profit")}"
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@FinancialDashboardActivity,
                            "Parse error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}