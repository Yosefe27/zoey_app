package com.siresystems.zoey_gardens_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.siresystems.zoey_gardens_app.adapter.CartAdapter
import com.siresystems.zoey_gardens_app.api.ApiClient
import com.siresystems.zoey_gardens_app.model.OrderResponse
import com.siresystems.zoey_gardens_app.utils.CartManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerCart: RecyclerView
    private lateinit var txtTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var txtEmpty: TextView

    private lateinit var radioLocation: RadioGroup
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerCart = findViewById(R.id.recyclerCart)
        txtTotal = findViewById(R.id.txtTotal)
        btnCheckout = findViewById(R.id.btnCheckout)
        txtEmpty = findViewById(R.id.txtEmpty)
        radioLocation = findViewById(R.id.radioLocation)

        recyclerCart.layoutManager = LinearLayoutManager(this)

        adapter = CartAdapter(CartManager.cartItems) {
            updateTotal()
            checkIfEmpty()
        }

        recyclerCart.adapter = adapter

        updateTotal()
        checkIfEmpty()

        btnCheckout.setOnClickListener {

            val locationInput = getSelectedLocation()

            if (locationInput.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please select Dining, Pick-Up or Delivery",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("location", locationInput)
            intent.putExtra("total", CartManager.getTotal())
            startActivity(intent)
        }
    }

    // ✅ GET SELECTED RADIO OPTION
    private fun getSelectedLocation(): String {

        return when (radioLocation.checkedRadioButtonId) {

            R.id.rbDining -> "Dining"
            R.id.rbPickup -> "Pick-Up"
            R.id.rbDelivery -> "Delivery"

            else -> ""
        }
    }

    private fun updateTotal() {
        txtTotal.text = "Total: K ${CartManager.getTotal()}"
    }

    private fun checkIfEmpty() {

        if (CartManager.cartItems.isEmpty()) {

            txtEmpty.visibility = View.VISIBLE
            recyclerCart.visibility = View.GONE
            btnCheckout.isEnabled = false

        } else {

            txtEmpty.visibility = View.GONE
            recyclerCart.visibility = View.VISIBLE
            btnCheckout.isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        updateTotal()
        checkIfEmpty()
    }

    // ✅ FINAL ORDER SUBMISSION
    private fun placeOrder() {

        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        val phone = prefs.getString("phone", "")?.trim() ?: ""

        if (phone.isEmpty()) {
            Toast.makeText(this, "User phone missing. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        if (CartManager.cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val locationInput = getSelectedLocation()

        if (locationInput.isEmpty()) {
            Toast.makeText(this, "Please select order type", Toast.LENGTH_SHORT).show()
            return
        }

        val itemsJson = Gson().toJson(CartManager.cartItems)
        val total = CartManager.cartItems.sumOf { it.price * it.quantity }

        btnCheckout.isEnabled = false
        btnCheckout.text = "Processing..."

        ApiClient.instance.saveOrder(
            phone,
            locationInput,
            itemsJson,
            total
        ).enqueue(object : Callback<OrderResponse> {

            override fun onResponse(
                call: Call<OrderResponse>,
                response: Response<OrderResponse>
            ) {

                btnCheckout.isEnabled = true
                btnCheckout.text = "Checkout"

                if (response.isSuccessful && response.body()?.status == "success") {

                    Toast.makeText(
                        this@CartActivity,
                        "Order placed successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    CartManager.cartItems.clear()
                    adapter.notifyDataSetChanged()

                    updateTotal()
                    checkIfEmpty()

                } else {

                    Toast.makeText(
                        this@CartActivity,
                        "Failed to place order",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {

                btnCheckout.isEnabled = true
                btnCheckout.text = "Checkout"

                Toast.makeText(
                    this@CartActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}