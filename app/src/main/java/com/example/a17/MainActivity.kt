package com.example.a17

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek apakah pengguna sudah login
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            // Jika belum login, buka LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Tutup MainActivity agar tidak bisa kembali ke halaman ini
            return
        }

        setContentView(R.layout.main_activity)

        // Inisialisasi ImageButton
        val imageButton = findViewById<ImageButton>(R.id.ButtonProfile)

        // Set OnClickListener pada ImageButton
        imageButton?.setOnClickListener {
            // Intent untuk berpindah ke ActivityProfil
            val intent = Intent(this, ActivityProfil::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chat -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_sensor -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SensorFragment())
                        .commit()
                    true
                }
                R.id.nav_volley_court -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, VollyFragment())
                        .commit()
                    true
                }
                R.id.nav_notifications -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, KontakFragment())
                        .commit()
                    true
                }
                R.id.nav_film -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, KontakFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
