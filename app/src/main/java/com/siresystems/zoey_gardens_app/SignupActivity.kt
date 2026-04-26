package com.siresystems.zoey_gardens_app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.siresystems.zoey_gardens_app.api.ApiClient
import com.siresystems.zoey_gardens_app.model.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val name = findViewById<EditText>(R.id.edtName)
        val phone = findViewById<EditText>(R.id.edtPhone)
        val pass = findViewById<EditText>(R.id.edtPassword)
        val btn = findViewById<Button>(R.id.btnSignup)

        btn.setOnClickListener {

            val userName = name.text.toString().trim()
            val userPhone = phone.text.toString().trim()
            val userPass = pass.text.toString().trim()

            // 🔒 VALIDATION
            if (userName.isEmpty()) {
                name.error = "Enter your name"
                name.requestFocus()
                return@setOnClickListener
            }

            if (userPhone.isEmpty()) {
                phone.error = "Enter phone number"
                phone.requestFocus()
                return@setOnClickListener
            }

            if (userPass.length < 4) {
                pass.error = "Password must be at least 4 characters"
                pass.requestFocus()
                return@setOnClickListener
            }

            btn.isEnabled = false
            btn.text = "Creating..."

            ApiClient.instance.signup(userName, userPhone, userPass)
                .enqueue(object : Callback<UserResponse> {

                    override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {

                        btn.isEnabled = true
                        btn.text = "Create Account"

                        if (response.isSuccessful && response.body() != null) {

                            val res = response.body()!!

                            if (res.status == "success") {
                                Toast.makeText(this@SignupActivity, "Account created successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@SignupActivity, res.message ?: "Signup failed", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(this@SignupActivity, "Server error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {

                        btn.isEnabled = true
                        btn.text = "Create Account"

                        Toast.makeText(this@SignupActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}