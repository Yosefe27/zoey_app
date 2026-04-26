package com.siresystems.zoey_gardens_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CheckoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val btnPay = findViewById<Button>(R.id.btnPay)

        btnPay.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }
    }
}