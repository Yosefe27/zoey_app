package com.siresystems.zoey_gardens_app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.siresystems.zoey_gardens_app.api.ApiClient
import com.siresystems.zoey_gardens_app.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WaiterLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Auto-login check
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        if (prefs.contains("user_id")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_waiter_login)

        val phone = findViewById<EditText>(R.id.edtUsername)
        val pass = findViewById<EditText>(R.id.edtAdminPassword)
        val btn = findViewById<Button>(R.id.btnAdminLogin)

        btn.setOnClickListener {

            val userPhone = phone.text.toString().trim()
            val userPass = pass.text.toString().trim()

            // 🔒 VALIDATION
            if (userPhone.isEmpty()) {
                phone.error = "Enter Username"
                phone.requestFocus()
                return@setOnClickListener
            }

            if (userPass.isEmpty()) {
                pass.error = "Enter password"
                pass.requestFocus()
                return@setOnClickListener
            }

            btn.isEnabled = false
            btn.text = "Logging in..."

            ApiClient.instance.adminlogin(userPhone, userPass)
                .enqueue(object : Callback<UserResponse> {

                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {

                        btn.isEnabled = true
                        btn.text = "Login"

                        if (response.isSuccessful && response.body() != null) {

                            val res = response.body()!!

                            if (res.status == "success") {

                                val prefs = getSharedPreferences("user", MODE_PRIVATE)
                                prefs.edit()
                                    .putInt("user_id", res.user_id!!)
                                    .putString("name", res.name)
                                    .putString("phone", userPhone)
                                    .apply()

                                Toast.makeText(this@WaiterLoginActivity, "Welcome ${res.name}", Toast.LENGTH_SHORT).show()

                                startActivity(Intent(this@WaiterLoginActivity, AdminDashboardActivity::class.java))
                                finish()

                            } else {
                                Toast.makeText(this@WaiterLoginActivity, res.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(this@WaiterLoginActivity, "Server error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {

                        btn.isEnabled = true
                        btn.text = "Login"

                        Toast.makeText(this@WaiterLoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}