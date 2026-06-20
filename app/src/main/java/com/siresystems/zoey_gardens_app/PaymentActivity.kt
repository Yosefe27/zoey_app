package com.siresystems.zoey_gardens_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.siresystems.zoey_gardens_app.api.ApiClient
import com.siresystems.zoey_gardens_app.model.OrderResponse
import com.siresystems.zoey_gardens_app.utils.CartManager
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.URLEncoder

class PaymentActivity : AppCompatActivity() {

    private lateinit var txtTotal: TextView
    private lateinit var btnPay: Button
    private lateinit var radioPayment: RadioGroup

    private var location = ""
    private var total = 0.0

    // =========================
    // ADMIN DETAILS
    // =========================
    private val adminWhatsApp = "260979336221"

    // CHANGE TO YOUR REAL URL
    private val emailApiUrl =
        "https://zoeygardens-001-site1.site4future.com/zoey_apis/send_order_email.php"

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

    // =========================
    // PROCESS PAYMENT
    // =========================
    private fun processPayment() {

        btnPay.text = "Processing..."
        btnPay.isEnabled = false

        Handler(Looper.getMainLooper()).postDelayed({

            Toast.makeText(
                this,
                "Your Order Has Been Placed",
                Toast.LENGTH_SHORT
            ).show()

            saveOrderToServer()

        }, 2000)
    }

    // =========================
    // SAVE ORDER
    // =========================
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

                    // SEND EMAIL TO ADMIN
                    sendEmailToServer(phone)

                    // CLEAR CART
                    CartManager.cartItems.clear()

                    Toast.makeText(
                        this@PaymentActivity,
                        "Order placed successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // SHOW WHATSAPP DIALOG
                    showWhatsAppDialog(phone)

                } else {

                    Toast.makeText(
                        this@PaymentActivity,
                        "Order failed",
                        Toast.LENGTH_SHORT
                    ).show()

                    btnPay.text = "Pay Now"
                    btnPay.isEnabled = true
                }
            }

            override fun onFailure(
                call: Call<OrderResponse>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@PaymentActivity,
                    "Network error",
                    Toast.LENGTH_SHORT
                ).show()

                Log.e("ORDER_ERROR", t.message ?: "Unknown error")

                btnPay.text = "Pay Now"
                btnPay.isEnabled = true
            }
        })
    }

    // =========================
    // SHOW WHATSAPP DIALOG
    // =========================
    private fun showWhatsAppDialog(phone: String) {

        AlertDialog.Builder(this)
            .setTitle("Notify Admin")
            .setMessage("Would you like to notify the admin on WhatsApp? Include Proof Of Payment if possible.")
            .setCancelable(false)

            // YES BUTTON
            .setPositiveButton("Yes") { _, _ ->

                openWhatsApp(phone)

                Handler(Looper.getMainLooper()).postDelayed({

                    startActivity(
                        Intent(
                            this@PaymentActivity,
                            MainActivity::class.java
                        )
                    )

                    finish()

                }, 1500)
            }

            // NO BUTTON
            .setNegativeButton("No") { _, _ ->

                startActivity(
                    Intent(
                        this@PaymentActivity,
                        MainActivity::class.java
                    )
                )

                finish()
            }

            .show()
    }

    // =========================
    // OPEN WHATSAPP
    // =========================
    private fun openWhatsApp(phone: String) {

        try {

            val message = """
                I just Placed An Order
                
                Customer Phone: $phone
                
                Option: $location
                
                Total Amount: K $total
                
            """.trimIndent()

            val url = "https://wa.me/$adminWhatsApp?text=" +
                    URLEncoder.encode(message, "UTF-8")

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)

            // OPEN DIRECTLY IN WHATSAPP
            intent.setPackage("com.whatsapp")

            startActivity(intent)

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "WhatsApp is not installed",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =========================
    // SEND EMAIL TO SERVER
    // =========================
    private fun sendEmailToServer(phone: String) {

        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("phone", phone)
            .add("location", location)
            .add("total", total.toString())
            .build()

        val request = Request.Builder()
            .url(emailApiUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {

            override fun onFailure(
                call: okhttp3.Call,
                e: IOException
            ) {

                runOnUiThread {

                    Toast.makeText(
                        this@PaymentActivity,
                        "Email notification failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Log.e("EMAIL_ERROR", e.message ?: "Unknown error")
            }

            override fun onResponse(
                call: okhttp3.Call,
                response: okhttp3.Response
            ) {

                val responseText = response.body?.string()

                Log.d("EMAIL_RESPONSE", responseText ?: "No response")

                response.close()
            }
        })
    }
}