package com.siresystems.zoey_gardens_app

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.siresystems.zoey_gardens_app.api.ApiClient
import com.siresystems.zoey_gardens_app.model.OrderResponse
import com.siresystems.zoey_gardens_app.utils.CartManager
import retrofit2.Call
import retrofit2.Response

import android.os.Handler
import retrofit2.Callback

class PaymentActivity : AppCompatActivity() {

    private lateinit var txtTotal: TextView
    private lateinit var btnPay: Button
    private lateinit var radioPayment: RadioGroup

    private var location = ""
    private var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        txtTotal = findViewById(R.id.txtTotal)
        btnPay = findViewById(R.id.btnPay)
        radioPayment = findViewById(R.id.radioPayment)

        location = intent.getStringExtra("location") ?: ""
        total = intent.getDoubleExtra("total", 0.0)

        txtTotal.text = "Total: K $total"

        btnPay.setOnClickListener {
            processPayment()
        }
    }

    private fun processPayment() {

        val selected = radioPayment.checkedRadioButtonId

        if (selected == -1) {
            Toast.makeText(this, "Select payment method", Toast.LENGTH_SHORT).show()
            return
        }

        btnPay.text = "Processing..."
        btnPay.isEnabled = false

        // 🔥 SIMULATE PAYMENT SUCCESS (replace with real API later)
        Handler(Looper.getMainLooper()).postDelayed({

            Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show()

            saveOrderToServer()

        }, 2000)
    }

    private fun saveOrderToServer() {

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val phone = prefs.getString("phone", "") ?: ""

        val itemsJson = Gson().toJson(CartManager.cartItems)

        ApiClient.instance.saveOrder(
            phone,
            location,
            itemsJson,
            total
        ).enqueue(object : Callback<OrderResponse> {

            override fun onResponse(
                call: Call<OrderResponse>,
                response: Response<OrderResponse>
            ) {

                if (response.isSuccessful) {

                    CartManager.cartItems.clear()

                    startActivity(Intent(this@PaymentActivity, MainActivity::class.java))
                    finish()

                } else {
                    Toast.makeText(this@PaymentActivity, "Order failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Toast.makeText(this@PaymentActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}