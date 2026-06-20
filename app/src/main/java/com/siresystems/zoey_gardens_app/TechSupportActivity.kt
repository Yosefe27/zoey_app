package com.siresystems.zoey_gardens_app

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class TechSupportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_support)

        // Close button
        val btnClose = findViewById<ImageButton>(R.id.btnClose)

        btnClose.setOnClickListener {
            finish()
        }
    }
}