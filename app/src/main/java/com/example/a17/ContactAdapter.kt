package com.example.a17

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Model untuk setiap kontak
data class Contact(val id: Int, val name: String, val phoneNumber: String)

class ContactAdapter(private var contacts: MutableList<Contact>) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // Interface untuk menangani klik edit dan hapus
    interface OnItemClickListener {
        fun onEditClick(contact: Contact)
        fun onDeleteClick(contact: Contact)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // ViewHolder untuk item kontak
    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.textViewPhoneNumber)
        val editButton: View = itemView.findViewById(R.id.buttonEdit) // Assuming you have this button in the layout
        val deleteButton: View = itemView.findViewById(R.id.buttonDelete) // Assuming you have this button in the layout

        init {
            // Listener untuk klik pada tombol edit
            editButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onEditClick(contacts[position])
                }
            }

            // Listener untuk klik pada tombol hapus
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onDeleteClick(contacts[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.nameTextView.text = contact.name
        holder.phoneNumberTextView.text = contact.phoneNumber
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    // Fungsi untuk memperbarui seluruh kontak setelah ada perubahan
    fun setContacts(newContacts: MutableList<Contact>) {
        contacts.clear()
        contacts.addAll(newContacts)
        notifyDataSetChanged()
    }

    // Fungsi untuk memperbarui kontak yang sudah ada
    fun updateContact(updatedContact: Contact) {
        val index = contacts.indexOfFirst { it.id == updatedContact.id }
        if (index != -1) {
            contacts[index] = updatedContact
            notifyItemChanged(index)
        }
    }

    // Fungsi untuk menghapus kontak dari daftar
    fun deleteContact(contact: Contact) {
        val index = contacts.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            contacts.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
