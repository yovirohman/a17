package com.example.a17

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textViewCreateAccount: TextView // Tambahkan ini

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewCreateAccount = findViewById(R.id.text_buat_akun) // Inisialisasi TextView

        buttonLogin.setOnClickListener {
            loginUser()
        }

        // Set OnClickListener untuk TextView
        textViewCreateAccount.setOnClickListener {
            // Buka RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)

        if (username == savedUsername && password == savedPassword) {
            // Simpan status login di SharedPreferences
            with(sharedPreferences.edit()) {
                putBoolean("is_logged_in", true)
                apply()
            }
            Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
            // Pindahkan ke halaman utama aplikasi
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
        }
    }
}