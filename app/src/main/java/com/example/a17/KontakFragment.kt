package com.example.a17

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import android.widget.Toast

class KontakFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var myDatabaseHelper: MyDatabaseHelper
    private var contacts: MutableList<Contact> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_kontak, container, false)

        // Inisialisasi RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inisialisasi MyDatabaseHelper
        myDatabaseHelper = MyDatabaseHelper(requireContext())

        // Ambil semua kontak dari database
        contacts = myDatabaseHelper.getAllContacts().toMutableList()
        contactAdapter = ContactAdapter(contacts)
        recyclerView.adapter = contactAdapter

        // Menangani klik item untuk mengedit kontak
        contactAdapter.setOnItemClickListener(object : ContactAdapter.OnItemClickListener {
            override fun onEditClick(contact: Contact) {
                showEditContactDialog(contact)
            }

            override fun onDeleteClick(contact: Contact) {
                // Hapus kontak dari database
                myDatabaseHelper.deleteContact(contact.id)
                // Perbarui daftar kontak
                contacts = myDatabaseHelper.getAllContacts().toMutableList()
                contactAdapter.setContacts(contacts)
            }
        })

        // Menangani tombol tambah kontak
        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val editTextPhoneNumber: EditText = view.findViewById(R.id.editTextPhoneNumber)
        val buttonAddContact: Button = view.findViewById(R.id.buttonAddContact)

        buttonAddContact.setOnClickListener {
            val name = editTextName.text.toString()
            val phoneNumber = editTextPhoneNumber.text.toString()
            if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                // Tambahkan kontak ke database
                val newContact = Contact(0, name, phoneNumber) // Pastikan ID 0 (atau bisa di-autogenerate oleh DB)
                myDatabaseHelper.addContact(newContact)

                // Perbarui daftar kontak
                contacts = myDatabaseHelper.getAllContacts().toMutableList()
                contactAdapter.setContacts(contacts)

                // Bersihkan field input
                editTextName.text.clear()
                editTextPhoneNumber.text.clear()
            }
        }

        // Tambahkan tombol export data
        val buttonExportData: Button = view.findViewById(R.id.buttonExportData)
        buttonExportData.setOnClickListener {
            exportDataToTextFile(requireContext())
        }

        return view
    }

    // Fungsi untuk menampilkan dialog edit kontak
    private fun showEditContactDialog(contact: Contact) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_contact, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editTextPhoneNumber = dialogView.findViewById<EditText>(R.id.editTextPhoneNumber)

        // Isi EditText dengan data kontak yang ingin diedit
        editTextName.setText(contact.name)
        editTextPhoneNumber.setText(contact.phoneNumber)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Kontak")
            .setView(dialogView) // Pastikan menggunakan view yang benar
            .setPositiveButton("Simpan") { dialog, _ ->
                val newName = editTextName.text.toString()
                val newPhoneNumber = editTextPhoneNumber.text.toString()

                // Perbarui kontak di database
                val updatedContact = Contact(contact.id, newName, newPhoneNumber)
                myDatabaseHelper.updateContact(contact.id, updatedContact)

                // Perbarui daftar kontak di RecyclerView
                contacts = myDatabaseHelper.getAllContacts().toMutableList()
                contactAdapter.setContacts(contacts)

                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // Fungsi untuk ekspor data ke file txt
    private fun exportDataToTextFile(context: Context) {
        val dbHelper = MyDatabaseHelper(context)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM contacts", null)

        // File tujuan
        val exportDir = context.getExternalFilesDir(null) // Menyimpan di direktori aplikasi
        val file = File(exportDir, "kontak_data.txt")

        try {
            val writer = FileWriter(file)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number")) // Pastikan ini sesuai

                    // Tulis data ke file
                    writer.append("ID: $id, Nama: $name, Nomor Telepon: $phoneNumber\n")
                } while (cursor.moveToNext())
            }

            writer.flush()
            writer.close()
            cursor.close()

            Log.d("ExportData", "Data berhasil diekspor ke ${file.absolutePath}")
            // Tambahkan Toast untuk menunjukkan keberhasilan
            Toast.makeText(context, "Data berhasil diekspor ke ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e("ExportData", "Gagal mengekspor data: ${e.message}")
            // Tambahkan Toast untuk menunjukkan kesalahan
            Toast.makeText(context, "Gagal mengekspor data: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            db.close()
        }
    }

}
