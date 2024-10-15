package com.example.a17

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var imageView: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi ImageButton untuk profile
        val buttonProfile = view.findViewById<ImageButton>(R.id.ButtonProfile)

        // Inisialisasi ImageButton untuk tambah status (kamera)
        val buttonTambahStatus = view.findViewById<ImageButton>(R.id.ButtonTambahStatus)

        // Inisialisasi ImageView untuk menampilkan gambar
        imageView = view.findViewById(R.id.imageView)



        // Set onClickListener untuk ImageButton profil
        buttonProfile?.setOnClickListener {
            // Intent untuk berpindah ke ActivityProfil
            val intent = Intent(requireActivity(), ActivityProfil::class.java)
            startActivity(intent)
        }

        // Set onClickListener untuk ImageButton tambah status (buka kamera)
        buttonTambahStatus?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                // Minta izin kamera jika belum diberikan
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            } else {
                // Izin sudah diberikan, buka kamera
                openCamera()
            }
        }

        // Menampilkan gambar dari database ketika fragment dibuka

        return view
    }

    // Fungsi untuk membuka kamera
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }

    // Tangani izin yang diminta secara runtime
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Izin diberikan, buka kamera
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Tangani hasil dari kamera setelah pengguna mengambil gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Tampilkan gambar di ImageView
            imageView.setImageBitmap(imageBitmap)


        }
    }

  }

