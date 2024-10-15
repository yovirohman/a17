package com.example.a17

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRepeatPassword: EditText
    private lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextRepeatPassword = findViewById(R.id.editTextRepeatPassword)
        buttonRegister = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val repeatPassword = editTextRepeatPassword.text.toString().trim()

        if (username.isEmpty()) {
            Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
        } else if (password != repeatPassword) {
            Toast.makeText(this, "Password dan konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
        } else if (!isPasswordValid(password)) {
            Toast.makeText(this, "Password harus terdiri dari minimal 8 karakter, termasuk huruf besar, huruf kecil, angka, dan simbol", Toast.LENGTH_LONG).show()
        } else {
            // Simpan data ke SharedPreferences
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("username", username)
            editor.putString("password", password)
            editor.apply()

            Toast.makeText(this, "Akun berhasil dibuat", Toast.LENGTH_SHORT).show()

            // Pindahkan pengguna ke halaman login
            finish() // Untuk kembali ke halaman login setelah registrasi berhasil
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        // Regex untuk password yang harus memiliki huruf besar, huruf kecil, angka, dan simbol
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    fun goToLogin(view: View) {
        // Pindahkan ke LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
