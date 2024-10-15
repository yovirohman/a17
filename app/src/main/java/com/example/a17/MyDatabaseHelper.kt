package com.example.a17

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "contacts.db"
        private const val DATABASE_VERSION = 2 // Naikkan versi untuk trigger onUpgrade()
        private const val TABLE_NAME = "contacts"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PHONE_NUMBER = "phone_number"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableStatement = ("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NAME TEXT, $COLUMN_PHONE_NUMBER TEXT)")
        db?.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    // Metode untuk menambahkan kontak
    fun addContact(contact: Contact) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, contact.name)
            put(COLUMN_PHONE_NUMBER, contact.phoneNumber)
        }
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    // Metode untuk mengambil semua kontak
    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val nameIndex = cursor.getColumnIndex(COLUMN_NAME)
            val phoneNumberIndex = cursor.getColumnIndex(COLUMN_PHONE_NUMBER)

            // Pastikan bahwa indeksnya tidak -1 sebelum mengakses data
            do {
                val id = if (idIndex != -1) cursor.getInt(idIndex) else -1
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else ""
                val phoneNumber = if (phoneNumberIndex != -1) cursor.getString(phoneNumberIndex) else ""
                contacts.add(Contact(id, name, phoneNumber)) // Menyimpan ID juga
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return contacts
    }

    // Metode untuk mengedit kontak berdasarkan ID
    fun updateContact(id: Int, contact: Contact): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, contact.name)
            put(COLUMN_PHONE_NUMBER, contact.phoneNumber)
        }
        // Mengembalikan jumlah baris yang diperbarui
        return db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Metode untuk menghapus kontak
    fun deleteContact(id: Int): Int {
        val db = this.writableDatabase
        // Mengembalikan jumlah baris yang dihapus
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}
